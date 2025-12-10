import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = 'http://localhost:8080/api/cart';

  constructor(private http: HttpClient) {}

  getCart(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}`);
  }

  addToCart(productId: number, quantity: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/add`, { productId, quantity });
  }

  updateCart(productId: number, quantity: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update`, { productId, quantity });
  }

  removeFromCart(productId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/remove/${productId}`);
  }
}