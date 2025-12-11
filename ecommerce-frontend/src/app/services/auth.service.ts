import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, switchMap } from 'rxjs/operators';

// Define User interface inline if the import is failing
interface User {
  id?: number;
  name: string;
  email: string;
  role?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  private currentUserSubject = new BehaviorSubject<any>(null);
  get currentUser$(): Observable<any> {
    return this.currentUserSubject.asObservable();
  }
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    if (this.isBrowser) {
      this.loadUserFromStorage();
    }
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<{ token: string; email: string; name: string }>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(res => {
          this.saveToken(res.token);
          const user = { email: res.email, name: res.name };
          this.saveUser(user);
          this.currentUserSubject.next(user);
        })
      );
  }
  loadUserFromStorage() {
    if (this.isBrowser) {
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        this.currentUserSubject.next(JSON.parse(savedUser));
      }
    }
  }

  register(name: string, email: string, password: string): Observable<any> {
    console.log('🔐 ========================================');
    console.log('🔐 [AuthService] REGISTER METHOD CALLED');
    console.log('🔐 ========================================');
    console.log('🔐 [AuthService] Name:', name);
    console.log('🔐 [AuthService] Email:', email);
    console.log('🔐 [AuthService] Password length:', password.length);
    console.log('🔐 [AuthService] API URL:', `${this.apiUrl}/register`);
    console.log('🔐 [AuthService] Request body:', { name, email, password: '***' });
    
    return this.http.post<{ token: string; email: string; name: string }>(`${this.apiUrl}/register`, { name, email, password })
      .pipe(
        tap({
          next: (res) => {
            console.log('✅ ========================================');
            console.log('✅ [AuthService] REGISTER HTTP SUCCESS');
            console.log('✅ ========================================');
            console.log('✅ [AuthService] Response:', res);
            console.log('✅ [AuthService] Response.token:', res.token);
            console.log('✅ [AuthService] Response.email:', res.email);
            console.log('✅ [AuthService] Response.name:', res.name);
            
            this.saveToken(res.token);
            console.log('✅ [AuthService] Token saved to localStorage');
            
            if (res.email && res.name) {
              const user = { email: res.email, name: res.name };
              this.saveUser(user);
              console.log('✅ [AuthService] User saved to localStorage:', user);
              
              this.currentUserSubject.next(user);
              console.log('✅ [AuthService] currentUserSubject updated');
            }
          },
          error: (err) => {
            console.error('❌ ========================================');
            console.error('❌ [AuthService] REGISTER HTTP ERROR');
            console.error('❌ ========================================');
            console.error('❌ [AuthService] Error:', err);
            console.error('❌ [AuthService] Error status:', err.status);
            console.error('❌ [AuthService] Error statusText:', err.statusText);
            console.error('❌ [AuthService] Error message:', err.message);
            console.error('❌ [AuthService] Error url:', err.url);
          }
        })
      );
  }

  saveToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('token', token);
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('token');
    }
    return null;
  }

  saveUser(user: User): void {
    if (this.isBrowser) {
      localStorage.setItem('user', JSON.stringify(user));
    }
  }

  getUser(): User | null {
    if (this.isBrowser) {
      const userJson = localStorage.getItem('user');
      if (userJson) {
        try {
          return JSON.parse(userJson);
        } catch {
          return null;
        }
      }
    }
    return null;
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}