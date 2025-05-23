import { http, HttpResponse } from 'msw';
import { data } from '../data/mock.data';
import { AppConstants } from '../../app/core/constants/app.constants';
import { User } from '../../app/core/models/user.model';
import { Modelo } from '../../app/core/models/modelo.model';

const modelos = new Map()

export const modeloHandlers = [
    // Handler para GET de todos usuários
    http.get(AppConstants.MODELO_URL, () => {
        return HttpResponse.json(Array.from(modelos.values()));
    }),


    http.post(AppConstants.MODELO_URL, async ({ request }) => {
        const newModelos = await request.json() as Modelo;
        modelos.set(newModelos.idModelo, newModelos);
        return HttpResponse.json(newModelos, { status: 201 });
    }),
];