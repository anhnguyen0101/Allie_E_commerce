import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

console.log('🔍 [main.server.ts] Module loaded');
console.log('🔍 [main.server.ts] Exporting bootstrap function');

const bootstrap = () => bootstrapApplication(AppComponent, config);

export default bootstrap;
