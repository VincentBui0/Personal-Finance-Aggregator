import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = '/api/auth';
  private platformId = inject(PLATFORM_ID);
  private http = inject(HttpClient);

  register(email: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.api}/register`, { email, password })
      .pipe(tap(res => this.setToken(res.token)));
  }

  login(email: string, password: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.api}/login`, { email, password })
      .pipe(tap(res => this.setToken(res.token)));
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem('token');
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private setToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) localStorage.setItem('token', token);
  }
}