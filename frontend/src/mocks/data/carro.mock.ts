import { Carro } from '../../app/core/models/carro.model';
import { mockMarca } from './marca.mock';
import { mockModelo } from './modelo.mock';

export const mockCarro: Carro = {
    idCarro: 1,
    marca: mockMarca,
    modelo: mockModelo,
    kmAdicionado: 50000,
    placa: 'ABC-1234',
    dtAdicionado: Date.now()
};