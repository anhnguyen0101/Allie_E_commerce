import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
})
export class Login {
  loginForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(
        this.loginForm.value.email,
        this.loginForm.value.password
      ).subscribe({
        next: (response) => {
          this.successMessage = 'Logged in successfully!';
          setTimeout(() => {
            this.router.navigate(['/products']);
          }, 500);
        },
        error: (err) => {
          if (err.status === 403) {
            this.errorMessage = 'Access denied: Invalid credentials or account not permitted.';
          } else if (err && err.error && err.error.message) {
            this.errorMessage = 'Login failed: ' + err.error.message;
          } else {
            this.errorMessage = 'Invalid email or password';
          }
        },
      });
    }
  }
}
