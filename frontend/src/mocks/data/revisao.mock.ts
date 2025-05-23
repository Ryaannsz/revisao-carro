import { Revisao } from '../../app/core/models/revisao.model';
import { mockUser } from './user.mock';
import { mockCarro } from './carro.mock';

export const mockRevisao: Revisao = {
    idRevisao: 1,
    kmAtual: 50500,
    user: mockUser,
    carro: mockCarro,
    dtRevisao: new Date('2024-10-15'),
};