import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar implements OnInit {
  currentUser$!: Observable<any>;
  searchQuery: string = '';
  wishlistCount: number = 0;
  cartCount: number = 0;
  showDropdown: boolean = false;
  showMobileMenu: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    console.log('🚀 [Navbar] CONSTRUCTOR CALLED');
  }

  ngOnInit(): void {
    console.log('🔍 [Navbar] Component initialized');
    console.log('🔍 [Navbar] Template file: ./navbar.html');
    
    this.currentUser$ = this.authService.currentUser$;
    
    this.currentUser$.subscribe(user => {
      console.log('🔍 [Navbar] Current user:', user);
    });
    
    // TODO: Get actual wishlist and cart counts from services
    this.wishlistCount = 3;
    this.cartCount = 5;
    
    console.log('🔍 [Navbar] Wishlist count:', this.wishlistCount);
    console.log('🔍 [Navbar] Cart count:', this.cartCount);
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
    this.router.navigate(['/']);
  }

  closeDropdown(): void {
    this.showDropdown = false;
  }
}
