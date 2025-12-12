import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Products } from './pages/products/products';
import { ProductDetail } from './pages/product-detail/product-detail';
import { Cart } from './pages/cart/cart';
import { Wishlist } from './pages/wishlist/wishlist';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

// ✅ Import admin components
import { AdminLayout } from './pages/admin/layout/admin-layout';
import { AdminDashboard } from './pages/admin/dashboard/admin-dashboard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'products', component: Products },
  { path: 'products/:id', component: ProductDetail },
  { path: 'cart', component: Cart, canActivate: [AuthGuard] },
  { path: 'wishlist', component: Wishlist, canActivate: [AuthGuard] },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  
  // ✅ Admin Routes - Protected by AdminGuard
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [AdminGuard],
    children: [
      { path: '', component: AdminDashboard }
    ]
  },
  
  { path: '**', redirectTo: '' }
];
