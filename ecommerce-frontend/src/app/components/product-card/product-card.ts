import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.html',
  styleUrls: ['./product-card.css']
})
export class ProductCard implements OnInit {
  @Input() product: any;
  
  isInWishlist: boolean = false;
  rating: number = 0;
  ratingStars: boolean[] = [];

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('🛒 [ProductCard] Product loaded:', this.product);
    // Generate random rating between 3.5 and 5 if not provided
    this.rating = this.product?.rating || (Math.random() * 1.5 + 3.5);
    this.generateStars();
    
    // TODO: Check if product is in wishlist from service
    this.isInWishlist = false;
  }

  generateStars(): void {
    const fullStars = Math.floor(this.rating);
    this.ratingStars = Array(5).fill(false).map((_, i) => i < fullStars);
  }

  toggleWishlist(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.isInWishlist = !this.isInWishlist;
    
    // TODO: Add/remove from wishlist service
    console.log(`Product ${this.product.id} ${this.isInWishlist ? 'added to' : 'removed from'} wishlist`);
  }

  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    
    console.log('🛒 ========================================');
    console.log('🛒 [ProductCard] ADD TO CART CLICKED');
    console.log('🛒 ========================================');
    console.log('🛒 [ProductCard] Product:', this.product);
    console.log('🛒 [ProductCard] Product ID:', this.product.id);
    console.log('🛒 [ProductCard] Product Name:', this.product.name);
    
    // Check authentication state
    const token = this.authService.getToken();
    const user = this.authService.getUser();
    
    console.log('🔐 ========================================');
    console.log('🔐 [ProductCard] AUTHENTICATION CHECK');
    console.log('🔐 ========================================');
    console.log('🔐 [ProductCard] Token exists:', !!token);
    console.log('🔐 [ProductCard] Token value:', token);
    console.log('🔐 [ProductCard] Token (first 50 chars):', token ? token.substring(0, 50) + '...' : 'NULL');
    console.log('🔐 [ProductCard] User:', user);
    console.log('🔐 [ProductCard] User email:', user?.email);
    console.log('🔐 [ProductCard] LocalStorage token:', localStorage.getItem('token'));
    console.log('🔐 [ProductCard] LocalStorage user:', localStorage.getItem('user'));
    
    if (!token) {
      console.error('❌ [ProductCard] NO TOKEN - Redirecting to login');
      alert('Please login first to add items to cart');
      this.router.navigate(['/login']);
      return;
    }
    
    console.log('🛒 ========================================');
    console.log('🛒 [ProductCard] CALLING CartService.addToCart()');
    console.log('🛒 ========================================');
    
    this.cartService.addToCart(this.product.id, 1).subscribe({
      next: (response) => {
        console.log('✅ ========================================');
        console.log('✅ [ProductCard] ADD TO CART SUCCESS');
        console.log('✅ ========================================');
        console.log('✅ [ProductCard] Response:', response);
        alert(`${this.product.name} added to cart!`);
      },
      error: (err) => {
        console.error('❌ ========================================');
        console.error('❌ [ProductCard] ADD TO CART ERROR');
        console.error('❌ ========================================');
        console.error('❌ [ProductCard] Error object:', err);
        console.error('❌ [ProductCard] Error status:', err.status);
        console.error('❌ [ProductCard] Error statusText:', err.statusText);
        console.error('❌ [ProductCard] Error message:', err.message);
        console.error('❌ [ProductCard] Error headers:', err.headers);
        console.error('❌ [ProductCard] Error body:', err.error);
        
        if (err.status === 401 || err.status === 403) {
          console.error('❌ [ProductCard] Authentication error - clearing session');
          alert('Your session expired. Please login again.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          alert(`Failed to add ${this.product.name} to cart. Please try again.`);
        }
      },
    });
  }
}
