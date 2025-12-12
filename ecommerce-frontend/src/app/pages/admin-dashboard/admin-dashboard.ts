import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';

interface AdminStats {
  totalProducts: number;
  totalOrders: number;
  totalUsers: number;
  pendingOrders: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-8">
      <h1 class="text-3xl font-bold mb-8">Dashboard Overview</h1>

      <div *ngIf="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>

      <div *ngIf="!loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <!-- Total Products -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <p class="text-gray-600 text-sm">Total Products</p>
          <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.totalProducts }}</p>
        </div>

        <!-- Total Orders -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <p class="text-gray-600 text-sm">Total Orders</p>
          <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.totalOrders }}</p>
        </div>

        <!-- Total Users -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <p class="text-gray-600 text-sm">Total Users</p>
          <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.totalUsers }}</p>
        </div>

        <!-- Pending Orders -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <p class="text-gray-600 text-sm">Pending Orders</p>
          <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.pendingOrders }}</p>
        </div>
      </div>
    </div>
  `
})
export class AdminDashboard implements OnInit {
  stats: AdminStats = {
    totalProducts: 0,
    totalOrders: 0,
    totalUsers: 0,
    pendingOrders: 0
  };
  loading = true;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.adminService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading stats:', err);
        this.loading = false;
      }
    });
  }
}