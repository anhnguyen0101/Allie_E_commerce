import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderResponse {
  orders: any[];
  order?: any;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  checkout(orderData: any): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}/checkout`, orderData);
  }

  getOrders(): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.apiUrl}`);
  }
}
