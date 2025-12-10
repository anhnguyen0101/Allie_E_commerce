import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductCard } from '../../components/product-card/product-card';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterModule, ProductCard],
  templateUrl: './products.html',
  styleUrls: ['./products.css']
})
export class Products implements OnInit {
  products: any[] = [];
  loading: boolean = true;
  error: string | null = null;
  
  // Pagination
  currentPage: number = 0;
  pageSize: number = 12;
  totalPages: number = 0;
  totalElements: number = 0;

  constructor(
    private productService: ProductService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    const startTime = performance.now();
    console.log('[Products] Loading page:', this.currentPage);
    
    this.loading = true;
    this.error = null;
    
    this.productService.getProducts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        const endTime = performance.now();
        console.log(`[Products] Loaded in ${(endTime - startTime).toFixed(2)}ms`);
        
        this.products = response.content || [];
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
        
        this.cdr.detectChanges();
        
        // Scroll to top only in browser (not during SSR)
        if (isPlatformBrowser(this.platformId)) {
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }
      },
      error: (err) => {
        console.error('[Products] Error:', err);
        this.error = 'Failed to load products.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
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

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadProducts();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  trackByProductId(index: number, product: any): number {
    return product.id;
  }
}
