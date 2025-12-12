import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-orders.html'
})
export class AdminOrders implements OnInit {
  orders: any[] = [];
  loading = true;

  statusOptions = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.adminService.getOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading orders:', err);
        alert('Failed to load orders');
        this.loading = false;
      }
    });
  }

  updateStatus(orderId: number, newStatus: string): void {
    if (confirm(`Change order status to ${newStatus}?`)) {
      this.adminService.updateOrderStatus(orderId, newStatus).subscribe({
        next: () => {
          alert('Order status updated successfully!');
          this.loadOrders();
        },
        error: (err) => {
          console.error('Error updating order status:', err);
          alert('Failed to update order status');
        }
      });
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'PROCESSING': return 'bg-blue-100 text-blue-800';
      case 'SHIPPED': return 'bg-purple-100 text-purple-800';
      case 'DELIVERED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }
}
