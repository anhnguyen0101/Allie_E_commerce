import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = 'http://localhost:8080/api/cart';
  
  // BehaviorSubject to broadcast cart count changes
  private cartCountSubject = new BehaviorSubject<number>(0);
  public cartCount$ = this.cartCountSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getCart(): Observable<any> {
    console.log('🛒 [CartService] Getting cart from:', this.apiUrl);
    return this.http.get<any>(this.apiUrl).pipe(
      tap(response => {
        console.log('✅ [CartService] Cart loaded:', response);
        // Update count whenever cart is fetched
        const count = response.items?.length || 0;
        this.cartCountSubject.next(count);
      }),
      tap({
        error: (err) => console.error('❌ [CartService] Error loading cart:', err)
      })
    );
  }

  addToCart(productId: number, quantity: number): Observable<any> {
    console.log('🛒 ========================================');
    console.log('🛒 [CartService] ADD TO CART SERVICE CALLED');
    console.log('🛒 ========================================');
    console.log('🛒 [CartService] Product ID:', productId);
    console.log('🛒 [CartService] Quantity:', quantity);
    console.log('🛒 [CartService] API URL:', this.apiUrl);
    
    // Check token before making request
    const token = this.authService.getToken();
    console.log('🔐 [CartService] Token exists:', !!token);
    console.log('🔐 [CartService] Token value:', token);
    console.log('🔐 [CartService] Token (first 50 chars):', token ? token.substring(0, 50) + '...' : 'NULL');
    
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
  
    console.log('🛒 [CartService] Request params:', params.toString());
    console.log('🛒 [CartService] Full URL:', `${this.apiUrl}?${params.toString()}`);
    console.log('🛒 [CartService] Request method: POST');
    console.log('🛒 [CartService] Request body: null');
    
    console.log('🛒 ========================================');
    console.log('🛒 [CartService] MAKING HTTP POST REQUEST');
    console.log('🛒 ========================================');
    
    return this.http.post<any>(this.apiUrl, null, { params }).pipe(
      tap({
        next: (response) => {
          console.log('✅ ========================================');
          console.log('✅ [CartService] HTTP REQUEST SUCCESS');
          console.log('✅ ========================================');
          console.log('✅ [CartService] Response:', response);
          console.log('✅ [CartService] Response type:', typeof response);
          console.log('✅ [CartService] Response keys:', Object.keys(response || {}));
        },
        error: (err) => {
          console.error('❌ ========================================');
          console.error('❌ [CartService] HTTP REQUEST FAILED');
          console.error('❌ ========================================');
          console.error('❌ [CartService] Error status:', err.status);
          console.error('❌ [CartService] Error statusText:', err.statusText);
          console.error('❌ [CartService] Error message:', err.message);
          console.error('❌ [CartService] Error url:', err.url);
          console.error('❌ [CartService] Error headers:', err.headers);
          console.error('❌ [CartService] Error body:', err.error);
          console.error('❌ [CartService] Full error object:', err);
        }
      })
    );
  }

  updateCart(productId: number, quantity: number): Observable<any> {
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());
    
    return this.http.put<any>(this.apiUrl, null, { params }).pipe(
      tap(() => {
        // Refresh count after update
        this.refreshCount();
      })
    );
  }

  removeFromCart(productId: number): Observable<any> {
    const params = new HttpParams().set('productId', productId.toString());
    return this.http.delete(this.apiUrl, { params }).pipe(
      tap(() => {
        // Decrement count immediately after successful remove
        const currentCount = this.cartCountSubject.value;
        this.cartCountSubject.next(Math.max(0, currentCount - 1));
      })
    );
  }

  // Method to manually refresh count
  refreshCount(): void {
    this.getCart().subscribe();
  }
}