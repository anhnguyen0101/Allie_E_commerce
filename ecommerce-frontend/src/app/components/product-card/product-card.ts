import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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

  ngOnInit(): void {
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
    
    // TODO: Add to cart service
    console.log(`Product ${this.product.id} added to cart`);
    
    // Show success feedback
    alert(`${this.product.name} added to cart!`);
  }
}
