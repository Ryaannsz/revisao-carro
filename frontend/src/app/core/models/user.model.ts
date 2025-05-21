import { Role } from './role.enum';

export interface User {
  idUser: number;
  cpf: string;
  senha: string;
  nome: string;
  roles: Role;
}
