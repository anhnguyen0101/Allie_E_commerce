import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Products } from './pages/products/products';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Wishlist } from './pages/wishlist/wishlist';
import { Cart } from './pages/cart/cart';
import { Orders } from './pages/orders/orders';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'products', component: Products },
  { path: 'wishlist', component: Wishlist },
  { path: 'cart', component: Cart },
  { path: 'orders', component: Orders },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: '**', redirectTo: '' }
];
