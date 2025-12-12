import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-product-form',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './admin-product-form.html'
})
export class AdminProductForm implements OnInit {
  productForm: FormGroup;
  categories: any[] = [];
  loading = false;
  isEditMode = false;
  productId: number | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  selectedFile: File | null = null;
  imagePreview: string | null = null;
  uploading = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.min(0.01)]],
      categoryId: ['', Validators.required],
      imageUrl: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    
    // Check if editing existing product
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.productId = Number(id);
      this.loadProduct(this.productId);
    }
  }

  loadCategories(): void {
    this.adminService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.errorMessage = 'Failed to load categories';
      }
    });
  }

  loadProduct(id: number): void {
    this.adminService.getProducts(0, 1000).subscribe({
      next: (response) => {
        const product = response.content.find((p: any) => p.id === id);
        if (product) {
          this.productForm.patchValue({
            name: product.name,
            description: product.description,
            price: product.price,
            categoryId: product.categoryId,
            imageUrl: product.imageUrl
          });
        }
      },
      error: (err) => {
        console.error('Error loading product:', err);
        this.errorMessage = 'Failed to load product';
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      
      // Show preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
      
      // Upload image immediately
      this.uploadImage(file);
    }
  }

  uploadImage(file: File): void {
    this.uploading = true;
    this.errorMessage = null;
    
    this.adminService.uploadImage(file).subscribe({
      next: (response) => {
        console.log('✅ Image uploaded:', response.imageUrl);
        this.productForm.patchValue({ imageUrl: response.imageUrl });
        this.uploading = false;
      },
      error: (err) => {
        console.error('❌ Error uploading image:', err);
        this.errorMessage = 'Failed to upload image. Please try again.';
        this.uploading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.loading = true;
      this.errorMessage = null;
      
      const productData = {
        ...this.productForm.value,
        price: Number(this.productForm.value.price)
      };

      const request$ = this.isEditMode && this.productId
        ? this.adminService.updateProduct(this.productId, productData)
        : this.adminService.createProduct(productData);

      request$.subscribe({
        next: () => {
          this.successMessage = this.isEditMode 
            ? 'Product updated successfully!' 
            : 'Product created successfully!';
          
          setTimeout(() => {
            this.router.navigate(['/admin/products']);
          }, 1500);
        },
        error: (err) => {
          console.error('Error saving product:', err);
          this.errorMessage = 'Failed to save product. Please try again.';
          this.loading = false;
        }
      });
    } else {
      this.errorMessage = 'Please fill in all required fields correctly.';
    }
  }

  cancel(): void {
    this.router.navigate(['/admin/products']);
  }
}
