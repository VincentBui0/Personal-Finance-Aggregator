import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BudgetService, Budget } from '../services/budget.service';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss'
})
export class BudgetComponent implements OnInit {
  budgets: Budget[] = [];
  loading = false;
  showForm = false;

  newCategory = '';
  newAmount = 0;
  newMonth = new Date().toISOString().substring(0, 7);

  categories = [
    'FOOD_AND_DRINK', 'TRANSPORTATION', 'TRAVEL', 'RENT_AND_UTILITIES',
    'GENERAL_MERCHANDISE', 'PERSONAL_CARE', 'LOAN_PAYMENTS',
    'TRANSFER_OUT', 'OTHER'
  ];

  constructor(private budgetService: BudgetService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.budgetService.getAll().subscribe({
      next: res => {
        this.budgets = res;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  save() {
    if (!this.newCategory || !this.newAmount) return;
    const month = this.newMonth + '-01';
    this.budgetService.create(this.newCategory, this.newAmount, month).subscribe({
      next: () => {
        this.showForm = false;
        this.newCategory = '';
        this.newAmount = 0;
        this.load();
      }
    });
  }

  delete(id: number) {
    this.budgetService.delete(id).subscribe(() => this.load());
  }

  formatMonth(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'long', year: 'numeric', timeZone: 'UTC' });
  }
}