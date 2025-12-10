import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'ecommerce-frontend';

  ngOnInit(): void {
    console.log('🚀 [AppComponent] App initialized');
    console.log('🚀 [AppComponent] Navbar component imported:', Navbar);
    console.log('🚀 [AppComponent] Navbar path: ./components/navbar/navbar');
  }
}
