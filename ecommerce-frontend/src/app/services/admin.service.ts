import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface AdminStats {
  totalProducts: number;
  totalOrders: number;
  totalUsers: number;
  pendingOrders: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = 'http://localhost:8081/api/admin'; // ✅ CHANGE TO 8081

  constructor(private http: HttpClient) {}

  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
  }

  // PRODUCTS
  getProducts(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get(`${this.apiUrl}/products?page=${page}&size=${size}`);
  }

  createProduct(product: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/products`, product);
  }

  updateProduct(id: number, product: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/products/${id}`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/products/${id}`);
  }

  // CATEGORIES
  getCategories(): Observable<any> {
    return this.http.get(`${this.apiUrl}/categories`);
  }

  createCategory(category: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/categories`, category);
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/categories/${id}`);
  }

  // ORDERS
  getOrders(): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/orders/${orderId}/status`, { status });
  }

  // USERS
  getUsers(): Observable<any> {
    return this.http.get(`${this.apiUrl}/users`);
  }

  promoteUser(userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${userId}/promote`, {});
  }

  deleteUser(userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${userId}`);
  }

  // ✅ ADD IMAGE UPLOAD METHOD
  uploadImage(file: File): Observable<{ imageUrl: string; filename: string }> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<{ imageUrl: string; filename: string }>(`${this.apiUrl}/upload/image`, formData);
  }
}