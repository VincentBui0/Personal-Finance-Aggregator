import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TransactionService, Transaction } from '../services/transaction.service';
import { PlaidService } from '../services/plaid.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  transactions: Transaction[] = [];
  spendingByCategory: { category: string; total: number }[] = [];
  loading = false;
  connectingBank = false;

  constructor(
    private txnService: TransactionService,
    private plaidService: PlaidService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions() {
    this.loading = true;
    this.txnService.getAll(0, 50).subscribe({
      next: res => {
        this.transactions = res.content;
        this.calculateSpending();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  calculateSpending() {
    const map = new Map<string, number>();
    this.transactions.forEach(t => {
      const cat = t.category || 'Other';
      map.set(cat, (map.get(cat) || 0) + Number(t.amount));
    });
    this.spendingByCategory = Array.from(map.entries())
      .map(([category, total]) => ({ category, total }))
      .sort((a, b) => b.total - a.total);
  }

  connectBank() {
    this.connectingBank = true;
    this.plaidService.getLinkToken().subscribe({
      next: ({ link_token }) => {
        const handler = (window as any).Plaid.create({
          token: link_token,
          onSuccess: (public_token: string) => {
            this.plaidService.exchangeToken(public_token).subscribe(() => {
              this.loadTransactions();
              this.connectingBank = false;
            });
          },
          onExit: () => { this.connectingBank = false; }
        });
        handler.open();
      },
      error: () => { this.connectingBank = false; }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}