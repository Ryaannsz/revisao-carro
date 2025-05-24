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
      .patchRole('ADMIN', user.idUser)
      .subscribe((updatedUser) => {
        this.loadUsers();
        this.toastService.showSuccess("Usuário promovido com sucesso!")
      });
  }

  demote(user: User) {
    if (!this.authService.getUserRole()) return this.toastService.showError("Permissões insuficientes.")
    this.userService.patchRole('USER', user.idUser).subscribe((updatedUser) => {
      this.loadUsers();
      this.toastService.showSuccess("Usuário rebaixado com sucesso!")
    });
  }
}
