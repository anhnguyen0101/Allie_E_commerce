import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('🔐 ========================================');
    console.log('🔐 [JwtInterceptor] INTERCEPTOR CALLED');
    console.log('🔐 ========================================');
    console.log('🔐 [JwtInterceptor] Request URL:', req.url);
    console.log('🔐 [JwtInterceptor] Request method:', req.method);
    console.log('🔐 [JwtInterceptor] Request headers:', req.headers.keys());
    console.log('🔐 [JwtInterceptor] Request params:', req.params.toString());
    console.log('🔐 [JwtInterceptor] Request body:', req.body);
    
    // Check if this is an auth endpoint
    const isAuthEndpoint = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register');
    console.log('🔐 [JwtInterceptor] Is auth endpoint:', isAuthEndpoint);
    console.log('🔐 [JwtInterceptor] URL includes /api/auth/login:', req.url.includes('/api/auth/login'));
    console.log('🔐 [JwtInterceptor] URL includes /api/auth/register:', req.url.includes('/api/auth/register'));
    
    if (isAuthEndpoint) {
      console.warn('⚠️ [JwtInterceptor] Skipping token for auth endpoint');
      console.log('🔐 ========================================');
      return next.handle(req);
    }

    // Get token from AuthService
    console.log('🔐 [JwtInterceptor] Getting token from AuthService...');
    const token = this.authService.getToken();
    
    console.log('🔐 ========================================');
    console.log('🔐 [JwtInterceptor] TOKEN CHECK');
    console.log('🔐 ========================================');
    console.log('🔐 [JwtInterceptor] Token exists:', !!token);
    console.log('🔐 [JwtInterceptor] Token type:', typeof token);
    console.log('🔐 [JwtInterceptor] Token length:', token ? token.length : 0);
    console.log('🔐 [JwtInterceptor] Token value:', token);
    console.log('🔐 [JwtInterceptor] Token (first 50 chars):', token ? token.substring(0, 50) + '...' : 'NULL');
    console.log('🔐 [JwtInterceptor] LocalStorage token:', localStorage.getItem('token'));
    
    if (token) {
      console.log('✅ [JwtInterceptor] Token found - cloning request with Authorization header');
      
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      
      console.log('✅ ========================================');
      console.log('✅ [JwtInterceptor] REQUEST CLONED');
      console.log('✅ ========================================');
      console.log('✅ [JwtInterceptor] Cloned request URL:', cloned.url);
      console.log('✅ [JwtInterceptor] Cloned request method:', cloned.method);
      console.log('✅ [JwtInterceptor] Cloned request headers:', cloned.headers.keys());
      console.log('✅ [JwtInterceptor] Authorization header exists:', cloned.headers.has('Authorization'));
      console.log('✅ [JwtInterceptor] Authorization header value:', cloned.headers.get('Authorization'));
      console.log('✅ [JwtInterceptor] Authorization header (first 50 chars):', 
                  cloned.headers.get('Authorization')?.substring(0, 50) + '...');
      
      console.log('✅ [JwtInterceptor] Sending cloned request...');
      console.log('🔐 ========================================');
      
      return next.handle(cloned).pipe(
        tap({
          next: (event) => {
            if (event.type === 0) {
              console.log('📤 [JwtInterceptor] Request sent');
            } else if (event.type === 4) {
              console.log('✅ ========================================');
              console.log('✅ [JwtInterceptor] RESPONSE RECEIVED');
              console.log('✅ ========================================');
              console.log('✅ [JwtInterceptor] Event type:', event.type);
              console.log('✅ [JwtInterceptor] Response:', event);
            }
          },
          error: (err) => {
            console.error('❌ ========================================');
            console.error('❌ [JwtInterceptor] REQUEST FAILED');
            console.error('❌ ========================================');
            console.error('❌ [JwtInterceptor] Error status:', err.status);
            console.error('❌ [JwtInterceptor] Error statusText:', err.statusText);
            console.error('❌ [JwtInterceptor] Error message:', err.message);
            console.error('❌ [JwtInterceptor] Error headers:', err.headers);
            console.error('❌ [JwtInterceptor] Full error:', err);
          }
        })
      );
    }
    
    console.error('❌ ========================================');
    console.error('❌ [JwtInterceptor] NO TOKEN FOUND');
    console.error('❌ ========================================');
    console.error('❌ [JwtInterceptor] Request will be sent WITHOUT Authorization header');
    console.error('❌ [JwtInterceptor] This WILL cause 401/403 error');
    console.log('🔐 ========================================');
    
    return next.handle(req);
  }
}
