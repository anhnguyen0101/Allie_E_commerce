import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { WishlistService } from '../../services/wishlist.service';
import { CartService } from '../../services/cart.service';
import { Observable, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit, OnDestroy {
  currentUser$!: Observable<any>;
  searchQuery: string = '';
  wishlistCount: number = 0;
  cartCount: number = 0;
  showDropdown: boolean = false;
  showMobileMenu: boolean = false;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private wishlistService: WishlistService,
    private cartService: CartService,
    private router: Router
  ) {
    console.log('🚀 [Navbar] CONSTRUCTOR CALLED');
  }

  ngOnInit(): void {
    console.log('🔍 [Navbar] Component initialized');
    console.log('🔍 [Navbar] Template file: ./navbar.html');
    
    this.currentUser$ = this.authService.currentUser$;
    
    // Subscribe to real-time wishlist count updates
    const wishlistSub = this.wishlistService.wishlistCount$.subscribe(count => {
      this.wishlistCount = count;
      console.log('📊 [Navbar] Wishlist count updated:', count);
    });
    this.subscriptions.add(wishlistSub);

    // Subscribe to real-time cart count updates
    const cartSub = this.cartService.cartCount$.subscribe(count => {
      this.cartCount = count;
      console.log('📊 [Navbar] Cart count updated:', count);
    });
    this.subscriptions.add(cartSub);

    // Load counts when user logs in
    const userSub = this.currentUser$.subscribe(user => {
      console.log('🔍 [Navbar] Current user:', user);
      if (user) {
        this.loadCounts();
      } else {
        this.wishlistCount = 0;
        this.cartCount = 0;
      }
    });
    this.subscriptions.add(userSub);

    // Reload counts after navigation (e.g., after adding to cart/wishlist)
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      if (this.authService.isLoggedIn()) {
        this.loadCounts();
      }
    });

    // Initial load if already logged in
    if (this.authService.isLoggedIn()) {
      this.loadCounts();
    }
  }

  ngOnDestroy(): void {
    // Clean up subscriptions to prevent memory leaks
    this.subscriptions.unsubscribe();
  }

  loadCounts(): void {
    console.log('📊 [Navbar] Loading initial wishlist and cart counts...');
    
    // Load wishlist count (will trigger wishlistCount$ update)
    this.wishlistService.getWishlist().subscribe({
      error: (err) => {
        console.error('❌ [Navbar] Error loading wishlist:', err);
      }
    });

    // Load cart count (will trigger cartCount$ update)
    this.cartService.getCart().subscribe({
      error: (err) => {
        console.error('❌ [Navbar] Error loading cart:', err);
      }
    });
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      console.log('Searching for:', this.searchQuery);
      this.router.navigate(['/products'], { queryParams: { search: this.searchQuery } });
    }
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  toggleMobileMenu(): void {
    this.showMobileMenu = !this.showMobileMenu;
  }

  logout(): void {
    this.authService.logout();
    this.showDropdown = false;
    this.wishlistCount = 0;
    this.cartCount = 0;
    this.router.navigate(['/']);
  }

  closeDropdown(): void {
    this.showDropdown = false;
  }
}
