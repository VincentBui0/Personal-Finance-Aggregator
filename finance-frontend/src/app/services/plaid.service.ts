import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PlaidService {
  private api = '/api/plaid';

  constructor(private http: HttpClient) {}

  getLinkToken(): Observable<{ link_token: string }> {
    return this.http.post<{ link_token: string }>(`${this.api}/link-token`, {});
  }

  exchangeToken(publicToken: string): Observable<{ status: string }> {
    return this.http.post<{ status: string }>(`${this.api}/exchange-token`, {
      public_token: publicToken
    });
  }
}