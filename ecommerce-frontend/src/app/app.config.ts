import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { routes } from './app.routes';
import { JwtInterceptor } from './interceptors/jwt.interceptor';

console.log('🔍 [app.config.ts] Creating appConfig');
console.log('🔍 [app.config.ts] Routes:', routes);

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ]
};

console.log('🔍 [app.config.ts] appConfig created:', appConfig);
console.log('🔍 [app.config.ts] appConfig providers:', appConfig.providers);
