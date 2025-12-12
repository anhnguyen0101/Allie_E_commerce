import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-users.html'
})
export class AdminUsers implements OnInit {
  users: any[] = [];
  loading = true;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.adminService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        alert('Failed to load users');
        this.loading = false;
      }
    });
  }

  promoteToAdmin(userId: number, userName: string): void {
    if (confirm(`Promote ${userName} to ADMIN?`)) {
      this.adminService.promoteUser(userId).subscribe({
        next: () => {
          alert('User promoted to ADMIN successfully!');
          this.loadUsers();
        },
        error: (err) => {
          console.error('Error promoting user:', err);
          alert('Failed to promote user');
        }
      });
    }
  }

  deleteUser(userId: number, userName: string): void {
    if (confirm(`Are you sure you want to delete user "${userName}"? This action cannot be undone.`)) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => {
          alert('User deleted successfully!');
          this.loadUsers();
        },
        error: (err) => {
          console.error('Error deleting user:', err);
          alert('Failed to delete user');
        }
      });
    }
  }
}
