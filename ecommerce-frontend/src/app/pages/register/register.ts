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
    
    console.log('📝 [Register] Component initialized');
  }

  onSubmit(): void {
    console.log('📝 ========================================');
    console.log('📝 [Register] REGISTRATION FORM SUBMITTED');
    console.log('📝 ========================================');
    console.log('📝 [Register] Form valid:', this.registerForm.valid);
    console.log('📝 [Register] Form values:', this.registerForm.value);
    console.log('📝 [Register] Form errors:', this.registerForm.errors);
    
    if (this.registerForm.valid) {
      const name = this.registerForm.value.name;
      const email = this.registerForm.value.email;
      const password = this.registerForm.value.password;
      
      console.log('📝 ========================================');
      console.log('📝 [Register] CALLING AuthService.register()');
      console.log('📝 ========================================');
      console.log('📝 [Register] Name:', name);
      console.log('📝 [Register] Email:', email);
      console.log('📝 [Register] Password length:', password.length);
      
      this.authService.register(name, email, password).subscribe({
        next: (response) => {
          console.log('✅ ========================================');
          console.log('✅ [Register] REGISTRATION SUCCESS');
          console.log('✅ ========================================');
          console.log('✅ [Register] Response:', response);
          console.log('✅ [Register] Response.token:', response.token);
          console.log('✅ [Register] Response.email:', response.email);
          console.log('✅ [Register] Response.name:', response.name);
          
          // Set user state in AuthService
          if (response && response.email && response.name) {
            this.authService["currentUserSubject"].next({ email: response.email, name: response.name });
            localStorage.setItem('user', JSON.stringify({ email: response.email, name: response.name }));
            console.log('✅ [Register] User saved to localStorage');
          }
          
          this.successMessage = 'Account registered successfully!';
          console.log('✅ [Register] Redirecting to /products in 1.2 seconds...');
          
          setTimeout(() => {
            this.router.navigate(['/products']);
          }, 1200);
        },
        error: (err) => {
          console.error('❌ ========================================');
          console.error('❌ [Register] REGISTRATION ERROR');
          console.error('❌ ========================================');
          console.error('❌ [Register] Error object:', err);
          console.error('❌ [Register] Error status:', err.status);
          console.error('❌ [Register] Error statusText:', err.statusText);
          console.error('❌ [Register] Error message:', err.message);
          console.error('❌ [Register] Error.error:', err.error);
          console.error('❌ [Register] Error.error.message:', err.error?.message);
          
          // Check for specific error messages from backend
          if (err && err.error && typeof err.error === 'string' && err.error.includes('Email already in use')) {
            this.errorMessage = 'This email is already registered. Please login instead or use a different email.';
          } else if (err && err.error && err.error.message && err.error.message.includes('Email already in use')) {
            this.errorMessage = 'This email is already registered. Please login instead or use a different email.';
          } else if (err.status === 0) {
            this.errorMessage = 'Registration failed: Cannot connect to server. Is the backend running on port 8080?';
          } else if (err.status === 403 || err.status === 409) {
            this.errorMessage = 'This email is already registered. Please login instead or use a different email.';
          } else if (err && err.error && err.error.message) {
            this.errorMessage = 'Registration failed: ' + err.error.message;
          } else if (err && err.message) {
            this.errorMessage = 'Registration failed: ' + err.message;
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
          }
          
          console.error('❌ [Register] Error message set to:', this.errorMessage);
        },
      });
    } else {
      console.warn('⚠️ [Register] Form is INVALID');
      console.warn('⚠️ [Register] Form errors:', this.registerForm.errors);
      console.warn('⚠️ [Register] Name errors:', this.registerForm.get('name')?.errors);
      console.warn('⚠️ [Register] Email errors:', this.registerForm.get('email')?.errors);
      console.warn('⚠️ [Register] Password errors:', this.registerForm.get('password')?.errors);
    }
  }
}
