import { http, HttpResponse } from 'msw';
import { data } from '../data/mock.data';
import { AppConstants } from '../../app/core/constants/app.constants';
import { User } from '../../app/core/models/user.model';
import { Marca } from '../../app/core/models/marca.model';

const marcas = new Map()

export const marcaHandlers = [
    // Handler para GET de todos usuários
    http.get(AppConstants.MARCA_URL, () => {
        return HttpResponse.json(Array.from(marcas.values()));
    }),


    http.post(AppConstants.MARCA_URL, async ({ request }) => {
        const newMarca = await request.json() as Marca;
        marcas.set(newMarca.idMarca, newMarca);
        return HttpResponse.json(newMarca, { status: 201 });
    }),
];