import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getStats(): Observable<{
    userCount: number;
    productCount: number;
    orderCount: number;
    totalRevenue: number;
    bestSellingProducts: Array<{ name: string; sales: number }>;
  }> {
    return this.http.get<{
      userCount: number;
      productCount: number;
      orderCount: number;
      totalRevenue: number;
      bestSellingProducts: Array<{ name: string; sales: number }>;
    }>(`${this.baseUrl}/stats`);
  }
}