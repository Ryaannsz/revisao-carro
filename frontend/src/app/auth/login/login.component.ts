import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../../core/services/login.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  mostrarSenha = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      senha: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  somenteDigitos(event: Event) {
    const input = event.target as HTMLInputElement;
    const v = input.value.replace(/\D/g, '').slice(0, 11);
    if (v !== input.value) this.loginForm.get('cpf')?.setValue(v);
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.loginService.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        const destino = this.route.snapshot.queryParamMap.get('returnUrl') || '/carros';
        this.router.navigateByUrl(destino);
      },
      error: (err) => {
        this.isLoading = false;
        this.error = err?.status === 0
          ? 'Sem conexão com o servidor. Verifique a internet e tente de novo.'
          : 'CPF ou senha incorretos.';
      }
    });
  }
}
