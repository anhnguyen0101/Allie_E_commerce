import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AdminService } from '../../../services/admin.service';
import { ProductService } from '../../../services/product.service';

@Component({
  selector: 'app-admin-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-product-form.html'
})
export class AdminProductForm implements OnInit {
  productForm: FormGroup;
  categories: any[] = [];
  isEditMode = false;
  productId: number | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required]],
      price: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', [Validators.required]],
      imageUrl: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    
    // Check if editing
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.productId = +params['id'];
        this.loadProduct(this.productId);
      }
    });
  }

  loadCategories(): void {
    this.adminService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        alert('Failed to load categories');
      }
    });
  }

  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          categoryId: product.categoryId,
          imageUrl: product.imageUrl
        });
      },
      error: (err) => {
        console.error('Error loading product:', err);
        alert('Failed to load product');
        this.router.navigate(['/admin/products']);
      }
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.loading = true;
      const productData = this.productForm.value;

      const request = this.isEditMode && this.productId
        ? this.adminService.updateProduct(this.productId, productData)
        : this.adminService.createProduct(productData);

      request.subscribe({
        next: () => {
          alert(`Product ${this.isEditMode ? 'updated' : 'created'} successfully!`);
          this.router.navigate(['/admin/products']);
        },
        error: (err) => {
          console.error('Error saving product:', err);
          alert(`Failed to ${this.isEditMode ? 'update' : 'create'} product`);
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/admin/products']);
  }
}
