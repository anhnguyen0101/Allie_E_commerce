import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.html',
  styleUrl: './cart.css'
})
export class Cart implements OnInit {
  cartItems: any[] = [];
  total: number = 0;

  constructor(
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cartItems = data.items || [];
        this.calculateTotal();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading cart:', err);
        // For demo, use empty array
        this.cartItems = [];
        this.calculateTotal();
        this.cdr.detectChanges();
      },
    });
  }

  calculateTotal(): void {
    this.total = this.cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  incrementQuantity(item: any): void {
    const newQuantity = item.quantity + 1;
    this.updateQuantity(item.productId, newQuantity.toString());
  }

  decrementQuantity(item: any): void {
    if (item.quantity > 1) {
      const newQuantity = item.quantity - 1;
      this.updateQuantity(item.productId, newQuantity.toString());
    }
  }

  updateQuantity(productId: number, quantity: string): void {
    const parsedQuantity = parseInt(quantity, 10);
    if (parsedQuantity < 1) return;

    this.cartService.updateCart(productId, parsedQuantity).subscribe({
      next: () => {
        this.loadCart();
      },
      error: (err) => {
        console.error('Error updating quantity:', err);
      },
    });
  }

  removeItem(productId: number): void {
    if (confirm('Remove this item from your cart?')) {
      this.cartService.removeFromCart(productId).subscribe({
        next: () => {
          this.loadCart();
        },
        error: (err) => {
          console.error('Error removing item:', err);
        },
      });
    }
  }

  trackByProductId(index: number, item: any): number {
    return item.productId || item.id;
  }
}
