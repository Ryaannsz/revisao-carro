import { Component, EventEmitter, Output } from '@angular/core';
import { User } from '../../core/models/user.model';
import { Role } from '../../core/models/role.enum';
import { UserService } from '../../core/services/user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { trigger, transition, style, animate } from '@angular/animations';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: false,
  selector: 'app-modal-usuario',
  templateUrl: './modal-usuario.component.html',
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('150ms ease-out', style({ opacity: 1 })),
      ]),
      transition(':leave', [animate('150ms ease-in', style({ opacity: 0 }))]),
    ]),
    trigger('slideInOut', [
      transition(':enter', [
        style({ transform: 'translateY(20px)', opacity: 0 }),
        animate(
          '150ms ease-out',
          style({ transform: 'translateY(0)', opacity: 1 })
        ),
      ]),
      transition(':leave', [
        animate(
          '150ms ease-in',
          style({ transform: 'translateY(20px)', opacity: 0 })
        ),
      ]),
    ]),
  ],
})
export class ModalUsuarioComponent {
  @Output() close = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<User>();

  userForm: FormGroup;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastService: ToastService
  ) {
    this.userForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      roles: ['USER'],
    });
  }

  submit() {
    if (this.userForm.invalid) return;

    this.userService.post(this.userForm.value).subscribe({
      next: () => {
        this.userCreated.emit();
        this.close.emit();
        this.toastService.showSuccess('Usuário criado com sucesso!');
      },
      error: (err) => {
        this.toastService.showError('Erro ao criar usuário!');
      },
    });
  }

  formatCpf(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 11) {
      value = value.substring(0, 11);
    }

    this.cpf?.setValue(value);
  }

  get nome() {
    return this.userForm.get('nome');
  }

  get cpf() {
    return this.userForm.get('cpf');
  }

  get senha() {
    return this.userForm.get('senha');
  }
}
