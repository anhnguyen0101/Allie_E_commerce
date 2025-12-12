import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    console.log('🛡️ [AdminGuard] ========================================');
    console.log('🛡️ [AdminGuard] Checking admin access...');
    console.log('🛡️ [AdminGuard] Target URL:', state.url);
    console.log('🛡️ [AdminGuard] Route path:', route.url);
    
    const user = this.authService.getUser();
    const isAdmin = user?.role === 'ADMIN';
    
    console.log('🛡️ [AdminGuard] User:', user);
    console.log('🛡️ [AdminGuard] User role:', user?.role);
    console.log('🛡️ [AdminGuard] Is admin?:', isAdmin);
    
    if (isAdmin) {
      console.log('✅ [AdminGuard] Access granted - continuing to:', state.url);
      console.log('🛡️ [AdminGuard] ========================================');
      return true;
    } else {
      console.warn('❌ [AdminGuard] Access denied - user is not admin');
      console.warn('❌ [AdminGuard] Redirecting to /login');
      console.log('🛡️ [AdminGuard] ========================================');
      
      alert('Access denied. Admin privileges required.');
      this.router.navigate(['/login']); // ✅ Redirect to login, NOT home
      return false;
    }
  }
}
