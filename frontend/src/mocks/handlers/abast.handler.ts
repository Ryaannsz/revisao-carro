import { http, HttpResponse } from "msw";
import { AppConstants } from "../../app/core/constants/app.constants";
import { Abast } from "../../app/core/models/abast.model";
import { mockAbast } from "../data/abast.mock";

const abasts = new Map<number, Abast>([
    [mockAbast.idAbast, mockAbast]
]);

export const abastHandlers = [
    // GET todos
    http.get(AppConstants.ABAST_URL, () => {
        return HttpResponse.json(Array.from(abasts.values()));
    }),

    // GET por ID
    http.get(`${AppConstants.ABAST_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        const abast = abasts.get(id);
        if (abast) {
            return HttpResponse.json(abast);
        } else {
            return HttpResponse.json({ message: 'Abast not found' }, { status: 404 });
        }
    }),

    // POST novo
    http.post(AppConstants.ABAST_URL, async ({ request }) => {
        const newAbast = await request.json() as Abast;
        abasts.set(newAbast.idAbast, newAbast);
        return HttpResponse.json(newAbast, { status: 201 });
    }),

    // PUT (atualizar existente)
    http.put(`${AppConstants.ABAST_URL}/:id`, async ({ params, request }) => {
        const id = Number(params['id']);
        if (!abasts.has(id)) {
            return HttpResponse.json({ message: 'Abast not found' }, { status: 404 });
        }

        const updatedAbast = await request.json() as Abast;
        abasts.set(id, updatedAbast);
        return HttpResponse.json(updatedAbast);
    }),

    // DELETE (remover por ID)
    http.delete(`${AppConstants.ABAST_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        if (abasts.has(id)) {
            abasts.delete(id);
            return HttpResponse.json({ message: 'Abast deleted' });
        } else {
            return HttpResponse.json({ message: 'Abast not found' }, { status: 404 });
        }
    }),

    http.get(`${AppConstants.ABAST_URL}/carro/:id`, ({ params }) => {
        const id = Number(params['id']);
        const abastsDoCarro = Array.from(abasts.values()).filter(a => a.carro.idCarro === id);

        return HttpResponse.json(abastsDoCarro);
    }),
];