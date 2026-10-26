import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Transaction {
  id: number;
  amount: number;
  category: string;
  merchant: string;
  txnDate: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private api = '/api/transactions';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 25, category?: string): Observable<PagedResponse<Transaction>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (category) params = params.set('category', category);
    return this.http.get<PagedResponse<Transaction>>(this.api, { params });
  }
}