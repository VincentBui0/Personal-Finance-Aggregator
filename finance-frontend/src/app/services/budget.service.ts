import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Budget {
  id: number;
  category: string;
  amount: number;
  month: string;
}

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private api = '/api/budgets';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.api);
  }

  create(category: string, amount: number, month: string): Observable<Budget> {
    return this.http.post<Budget>(this.api, { category, amount: amount.toString(), month });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}