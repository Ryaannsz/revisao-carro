import { Role } from './role.enum';

export interface User {
  id: number;
  cpf: string;
  senha: string;
  nome: string;
  roles: Role;
}
