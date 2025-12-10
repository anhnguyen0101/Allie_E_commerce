import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.html',
})
export class Register {
  registerForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.authService.register(
        this.registerForm.value.name,
        this.registerForm.value.email,
        this.registerForm.value.password
      ).subscribe({
        next: (response) => {
          // Set user state in AuthService
          if (response && response.email && response.name) {
            this.authService["currentUserSubject"].next({ email: response.email, name: response.name });
            localStorage.setItem('user', JSON.stringify({ email: response.email, name: response.name }));
          }
          this.successMessage = 'Account registered successfully!';
          setTimeout(() => {
            this.router.navigate(['/products']);
          }, 1200);
        },
        error: (err) => {
          if (err && err.error && err.error.message) {
            this.errorMessage = 'Registration failed: ' + err.error.message;
          } else if (err && err.message) {
            this.errorMessage = 'Registration failed: ' + err.message;
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
          }
        },
      });
    }
  }
}
