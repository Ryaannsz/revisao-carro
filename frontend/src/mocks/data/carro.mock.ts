import { CarroStatus } from '../../app/core/models/carro-status.enum';
import { Carro } from '../../app/core/models/carro.model';
import { mockMarca } from './marca.mock';
import { mockModelo } from './modelo.mock';

export const mockCarro: Carro = {
    id: 1,
    marca: mockMarca,
    modelo: mockModelo,
    kmAdicionado: 50000,
    placa: 'ABC-1234',
    dtAdicionado: Date.now(),
    alarmeRevisaoKm: 10000,
    alarmeRevisaoMes: 6,
    status: CarroStatus.DISPONIVEL
};