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

// ✅ Import ALL admin components
import { AdminLayout } from './pages/admin/layout/admin-layout';
import { AdminDashboard } from './pages/admin/dashboard/admin-dashboard';
import { AdminProducts } from './pages/admin/products/admin-products';
import { AdminProductForm } from './pages/admin/products/admin-product-form';
import { AdminOrders } from './pages/admin/orders/admin-orders';
import { AdminUsers } from './pages/admin/users/admin-users';

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
      { path: '', component: AdminDashboard },                    // /admin
      { path: 'products', component: AdminProducts },             // /admin/products
      { path: 'products/add', component: AdminProductForm },      // /admin/products/add
      { path: 'products/edit/:id', component: AdminProductForm }, // /admin/products/edit/:id
      { path: 'orders', component: AdminOrders },                 // /admin/orders
      { path: 'users', component: AdminUsers }                    // /admin/users
    ]
  },
  
  { path: '**', redirectTo: '' }
];
