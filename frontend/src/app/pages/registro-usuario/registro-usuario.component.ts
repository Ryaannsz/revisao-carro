import { Component, OnInit } from '@angular/core';
import { User } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';

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

  constructor(private userService: UserService) {}

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
    this.users.push(user);
    this.closeModal();
  }

  promote(user: User) {
    //this.userService.promoteToAdmin(user.idUser).subscribe((updated) => {
    // const idx = this.users.findIndex((u) => u.idUser === user.idUser);
    //  this.users[idx] = updated;
    // });
  }
}
