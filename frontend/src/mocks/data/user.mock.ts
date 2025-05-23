import { User } from "../../app/core/models/user.model";
import { Role } from '../../app/core/models/role.enum';

export const mockUser: User = {
    idUser: 1,
    cpf: '12345678900',
    senha: 'senha123',
    nome: 'Ryan Carvalho',
    roles: Role.USER
};
