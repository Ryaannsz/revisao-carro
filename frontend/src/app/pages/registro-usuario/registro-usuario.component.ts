import { Component, OnInit } from '@angular/core';
import { User } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-registro-usuario',
  standalone: false,
  templateUrl: './registro-usuario.component.html',
  styleUrl: './registro-usuario.component.css',
})
export class RegistroUsuarioComponent implements OnInit {
  users: User[] = [];
  showModal = false;
  filteredUsers: User[] = this.users;

  constructor(private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService
  ) { }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getList<User>().subscribe((users) => {
      this.users = users;
      this.filteredUsers = this.users;
    });
  }

  filterUsers(event: any) {
    const searchTerm = event.target.value.toLowerCase();
    this.filteredUsers = this.users.filter(
      (user) =>
        user.nome.toLowerCase().includes(searchTerm) ||
        user.cpf.includes(searchTerm)
    );
  }

  iniciais(nome: string): string {
    return (nome ?? '').trim().split(/\s+/).slice(0, 2).map(p => p[0] ?? '').join('').toUpperCase() || '?';
  }

  formatarCpf(cpf: string): string {
    const d = (cpf ?? '').replace(/\D/g, '');
    return d.length === 11 ? `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}` : cpf;
  }

  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  onUserCreated(user: User) {
    this.closeModal();
    this.loadUsers();
  }

  promote(user: User) {
    if (!this.authService.getUserRole()) return this.toastService.showError("Permissões insuficientes.")
    this.userService
      .patchRole('ADMIN', user.id)
      .subscribe((updatedUser) => {
        this.loadUsers();
        this.toastService.showSuccess(`${user.nome} agora é administrador.`)
      });
  }

  demote(user: User) {
    if (!this.authService.getUserRole()) return this.toastService.showError("Permissões insuficientes.")
    this.userService.patchRole('USER', user.id).subscribe((updatedUser) => {
      this.loadUsers();
      this.toastService.showSuccess(`${user.nome} agora é operador.`)
    });
  }
}
