import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TransactionService, Transaction } from '../services/transaction.service';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss'
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 25;
  selectedCategory = '';
  loading = false;

  categories = [
    '', 'FOOD_AND_DRINK', 'TRANSPORTATION', 'TRAVEL', 'RENT_AND_UTILITIES',
    'GENERAL_MERCHANDISE', 'PERSONAL_CARE', 'LOAN_PAYMENTS',
    'TRANSFER_OUT', 'TRANSFER_IN', 'OTHER'
  ];

  constructor(private txnService: TransactionService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.txnService.getAll(this.currentPage, this.pageSize,
        this.selectedCategory || undefined).subscribe({
      next: res => {
        this.transactions = res.content;
        this.totalElements = res.totalElements;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onCategoryChange() {
    this.currentPage = 0;
    this.load();
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.load();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.load();
    }
  }
}