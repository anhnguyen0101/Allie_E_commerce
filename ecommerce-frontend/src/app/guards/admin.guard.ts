import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    console.log('🛡️ [AdminGuard] Checking admin access...');
    
    const user = this.authService.getUser();
    const isAdmin = user?.role === 'ADMIN';
    
    console.log('🛡️ [AdminGuard] User:', user);
    console.log('🛡️ [AdminGuard] User role:', user?.role);
    console.log('🛡️ [AdminGuard] Is admin?:', isAdmin);
    
    if (isAdmin) {
      console.log('✅ [AdminGuard] Access granted');
      return true;
    } else {
      console.warn('❌ [AdminGuard] Access denied - redirecting to home');
      alert('Access denied. Admin privileges required.');
      this.router.navigate(['/']);
      return false;
    }
  }
}
