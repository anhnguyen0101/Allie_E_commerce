import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-products.html'
})
export class AdminProducts implements OnInit {
  products: any[] = [];
  loading = true;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0; // ✅ ADD THIS PROPERTY

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    console.log('🔍 [AdminProducts] Loading products...'); // ✅ ADD THIS
    console.log('🔍 [AdminProducts] Page:', this.currentPage, 'Size:', 20);
    
    this.loading = true;
    this.adminService.getProducts(this.currentPage, 20).subscribe({
      next: (response) => {
        console.log('✅ [AdminProducts] Response received:', response); // ✅ ADD THIS
        console.log('✅ [AdminProducts] Response.content:', response.content);
        console.log('✅ [AdminProducts] Response.totalElements:', response.totalElements);
        
        this.products = response.content || [];
        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElements || 0;
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ [AdminProducts] Error loading products:', err); // ✅ ADD THIS
        console.error('❌ [AdminProducts] Error status:', err.status);
        console.error('❌ [AdminProducts] Error message:', err.message);
        this.loading = false;
      }
    });
  }

  deleteProduct(id: number): void { // ✅ ONLY ONE PARAMETER
    if (confirm('Are you sure you want to delete this product?')) {
      this.adminService.deleteProduct(id).subscribe({
        next: () => {
          alert('Product deleted successfully');
          this.loadProducts();
        },
        error: (err) => {
          console.error('Error deleting product:', err);
          alert('Failed to delete product');
        }
      });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }
}
