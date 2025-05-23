import { Abast } from '../../app/core/models/abast.model';
import { mockUser } from './user.mock';
import { mockCarro } from './carro.mock';

export const mockAbast: Abast = {
    idAbast: 1,
    litroComb: 40,
    valorComb: 250,
    kmAtual: 50500,
    user: mockUser,
    dtAbast: new Date('2024-10-10'),
    carro: mockCarro
};