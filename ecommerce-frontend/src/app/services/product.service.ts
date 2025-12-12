import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductResponse {
  products: any[];
  total: number;
  page: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = 'http://localhost:8081/api/products'; // ✅ CHANGE TO 8081

  constructor(private http: HttpClient) {}

  getProducts(page: number = 0, size: number = 12, search?: string, categoryId?: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search) {
      params = params.set('search', search);
    }
    if (categoryId) {
      params = params.set('category', categoryId.toString());
    }

    return this.http.get<any>(this.apiUrl, { params });
  }

  getProductById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}