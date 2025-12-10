import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductCard } from '../../components/product-card/product-card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ProductCard],
  templateUrl: './home.html',  // ← Make sure it's 'home.html' NOT 'home.component.html'
  styleUrls: ['./home.css']
})
export class Home implements OnInit {
  featuredProducts: any[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFeaturedProducts();
  }

  loadFeaturedProducts(): void {
    const startTime = performance.now();
    console.log('[Home] Starting to load products...');
    console.log('[Home] Initial loading state:', this.loading);
    
    this.loading = true;
    this.error = null;
    
    this.productService.getProducts(0, 6).subscribe({
      next: (response) => {
        const endTime = performance.now();
        console.log(`[Home] Products loaded in ${(endTime - startTime).toFixed(2)}ms`);
        console.log('[Home] Response:', response);
        
        this.featuredProducts = response.content || [];
        this.loading = false;
        
        // Force change detection
        this.cdr.detectChanges();
        
        console.log('[Home] After setting loading to false:', this.loading);
        console.log('[Home] Products count:', this.featuredProducts.length);
      },
      error: (err) => {
        const endTime = performance.now();
        console.error(`[Home] Error after ${(endTime - startTime).toFixed(2)}ms:`, err);
        
        this.error = 'Failed to load products. Please make sure the backend is running.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  trackByProductId(index: number, product: any): number {
    return product.id;
  }
}
