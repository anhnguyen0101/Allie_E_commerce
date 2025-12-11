import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private apiUrl = 'http://localhost:8080/api/wishlist';
  
  // BehaviorSubject to broadcast wishlist count changes
  private wishlistCountSubject = new BehaviorSubject<number>(0);
  public wishlistCount$ = this.wishlistCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getWishlist(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      tap(items => {
        // Update count whenever wishlist is fetched
        this.wishlistCountSubject.next(items.length);
      })
    );
  }

  addToWishlist(productId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${productId}`, {}).pipe(
      tap(() => {
        // Increment count immediately after successful add
        const currentCount = this.wishlistCountSubject.value;
        this.wishlistCountSubject.next(currentCount + 1);
      })
    );
  }

  removeFromWishlist(productId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${productId}`).pipe(
      tap(() => {
        // Decrement count immediately after successful remove
        const currentCount = this.wishlistCountSubject.value;
        this.wishlistCountSubject.next(Math.max(0, currentCount - 1));
      })
    );
  }

  // Method to manually refresh count
  refreshCount(): void {
    this.getWishlist().subscribe();
  }
}