import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { routes } from './app.routes';
import { JwtInterceptor } from './interceptors/jwt.interceptor';

console.log('🔍 [app.config.ts] Creating appConfig');
console.log('🔍 [app.config.ts] Routes:', routes);
console.log('🔍 [app.config.ts] JWT Interceptor:', JwtInterceptor);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { 
      provide: HTTP_INTERCEPTORS, 
      useClass: JwtInterceptor, 
      multi: true 
    }
  ]
};

console.log('🔍 [app.config.ts] appConfig created with JWT interceptor registered');
console.log('🔍 [app.config.ts] Interceptor will attach Authorization header to all requests');
