import { http, HttpResponse } from 'msw';
import { data } from '../data/mock.data';
import { AppConstants } from '../../app/core/constants/app.constants';
import { Carro } from '../../app/core/models/carro.model';
import { mockCarro } from '../data/carro.mock';

const carros = new Map<number, Carro>([
    [mockCarro.idCarro, mockCarro]
]);

export const carroHandlers = [
    // Handler para GET de todos carros
    http.get(AppConstants.CARRO_URL, () => {
        return HttpResponse.json(Array.from(carros.values()));
    }),

    // Handler para GET de carros por id
    http.get(`${AppConstants.CARRO_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        const carro = carros.get(id);
        if (carro) {
            return HttpResponse.json(carro);
        } else {
            return HttpResponse.json({ message: 'Carro not found' }, { status: 404 });
        }
    }),

    http.post(AppConstants.CARRO_URL, async ({ request }) => {
        const newCarro = await request.json() as Carro;
        carros.set(newCarro.idCarro, newCarro);
        return HttpResponse.json(newCarro, { status: 201 });
    }),

    http.put(`${AppConstants.CARRO_URL}/:id`, async ({ params, request }) => {
        const id = Number(params['id']);
        if (!carros.has(id)) {
            return HttpResponse.json({ message: 'Carro não encontrada' }, { status: 404 });
        }

        const carroAtualizada = await request.json() as Carro;
        carros.set(id, carroAtualizada);
        return HttpResponse.json(carroAtualizada);
    }),
    http.delete(`${AppConstants.CARRO_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        if (carros.has(id)) {
            carros.delete(id);
            return HttpResponse.json({ message: 'Carro removida com sucesso' });
        } else {
            return HttpResponse.json({ message: 'Carro não encontrada' }, { status: 404 });
        }
    }),
];