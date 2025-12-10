import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { WishlistService } from '../../services/wishlist.service';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductCard } from '../../components/product-card/product-card';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule, ProductCard],
  templateUrl: './wishlist.html',
  styleUrl: './wishlist.css',
})
export class Wishlist implements OnInit {
  wishlistItems: any[] = [];

  constructor(
    private wishlistService: WishlistService, 
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadWishlist();
  }

  loadWishlist(): void {
    this.wishlistService.getWishlist().subscribe({
      next: (data) => {
        this.wishlistItems = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading wishlist:', err);
        // For demo purposes, use empty array
        this.wishlistItems = [];
        this.cdr.detectChanges();
      },
    });
  }

  addToCart(item: any): void {
    this.cartService.addToCart(item.productId, 1).subscribe({
      next: () => {
        alert(`${item.name} added to cart!`);
      },
      error: (err) => {
        console.error('Error adding to cart:', err);
      },
    });
  }

  removeFromWishlist(productId: number): void {
    if (confirm('Remove this item from your wishlist?')) {
      this.wishlistService.removeFromWishlist(productId).subscribe({
        next: () => {
          this.loadWishlist();
        },
        error: (err) => {
          console.error('Error removing item from wishlist:', err);
        },
      });
    }
  }

  clearAllWishlist(): void {
    if (confirm('Are you sure you want to clear all items from your wishlist?')) {
      // TODO: Call service to clear all wishlist items
      this.wishlistItems = [];
      this.cdr.detectChanges();
      alert('Wishlist cleared!');
    }
  }

  addAllToCart(): void {
    if (confirm('Add all wishlist items to your cart?')) {
      // TODO: Implement add all to cart
      alert('All items added to cart!');
    }
  }

  trackByProductId(index: number, item: any): number {
    return item.productId || item.id;
  }
}
