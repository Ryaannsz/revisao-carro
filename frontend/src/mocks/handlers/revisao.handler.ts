import { http, HttpResponse } from "msw";
import { AppConstants } from "../../app/core/constants/app.constants";
import { Revisao } from "../../app/core/models/revisao.model";
import { mockRevisao } from "../data/revisao.mock";

const revisoes = new Map<number, Revisao>([
    [mockRevisao.idRevisao, mockRevisao]
]);

export const revisaoHandlers = [
    // GET todas as revisões
    http.get(AppConstants.REVISAO_URL, () => {
        return HttpResponse.json(Array.from(revisoes.values()));
    }),

    // GET revisão por ID
    http.get(`${AppConstants.REVISAO_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        const revisao = revisoes.get(id);
        if (revisao) {
            return HttpResponse.json(revisao);
        } else {
            return HttpResponse.json({ message: 'Revisão não encontrada' }, { status: 404 });
        }
    }),

    // POST nova revisão
    http.post(AppConstants.REVISAO_URL, async ({ request }) => {
        const novaRevisao = await request.json() as Revisao;
        revisoes.set(novaRevisao.idRevisao, novaRevisao);
        return HttpResponse.json(novaRevisao, { status: 201 });
    }),

    // PUT atualizar revisão existente
    http.put(`${AppConstants.REVISAO_URL}/:id`, async ({ params, request }) => {
        const id = Number(params['id']);
        if (!revisoes.has(id)) {
            return HttpResponse.json({ message: 'Revisão não encontrada' }, { status: 404 });
        }

        const revisaoAtualizada = await request.json() as Revisao;
        revisoes.set(id, revisaoAtualizada);
        return HttpResponse.json(revisaoAtualizada);
    }),

    // DELETE remover revisão
    http.delete(`${AppConstants.REVISAO_URL}/:id`, ({ params }) => {
        const id = Number(params['id']);
        if (revisoes.has(id)) {
            revisoes.delete(id);
            return HttpResponse.json({ message: 'Revisão removida com sucesso' });
        } else {
            return HttpResponse.json({ message: 'Revisão não encontrada' }, { status: 404 });
        }
    }),
    http.get(`${AppConstants.REVISAO_URL}/carro/:id`, ({ params }) => {
        const id = Number(params['id']);
        const revisoesDoCarro = Array.from(revisoes.values()).filter(a => a.carro.idCarro === id);

        return HttpResponse.json(revisoesDoCarro);
    }),
];
