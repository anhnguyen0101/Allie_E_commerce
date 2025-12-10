import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { WishlistService } from '../../services/wishlist.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.html',
})
export class ProductDetail implements OnInit {
  product: any = null;
  loading = true;
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productService.getProductById(Number(id)).subscribe({
        next: (data) => {
          this.product = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error fetching product.';
          this.loading = false;
        },
      });
    } else {
      this.error = 'Invalid product ID.';
      this.loading = false;
    }
  }

  addToCart(): void {
    if (this.product) {
      this.cartService.addToCart(this.product.id, 1).subscribe({
        next: () => {
          this.successMessage = 'Added to cart!';
        },
        error: () => {
          this.successMessage = 'Failed to add to cart.';
        }
      });
    }
  }

  addToWishlist(): void {
    if (this.product) {
      this.wishlistService.addToWishlist(this.product.id).subscribe({
        next: () => {
          this.successMessage = 'Added to wishlist!';
        },
        error: () => {
          this.successMessage = 'Failed to add to wishlist.';
        }
      });
    }
  }
}
