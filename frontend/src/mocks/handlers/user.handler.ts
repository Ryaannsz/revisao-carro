import { http, HttpResponse } from 'msw';
import { data } from '../data/mock.data';
import { AppConstants } from '../../app/core/constants/app.constants';
import { User } from '../../app/core/models/user.model';

const users = new Map()

export const userHandlers = [
  // Handler para GET de todos usuários
  http.get(AppConstants.USER_URL, () => {
    return HttpResponse.json(Array.from(users.values()));
  }),

  // Handler para GET de usuário por id
  http.get(`${AppConstants.USER_URL}/:id`, ({ params }) => {
    const id = Number(params['id']);
    const user = users.get(id);
    if (user) {
      return HttpResponse.json(user);
    } else {
      return HttpResponse.json({ message: 'User not found' }, { status: 404 });
    }
  }),

  http.post(AppConstants.USER_URL, async ({ request }) => {
    const newUser = await request.json() as User;
    users.set(newUser.idUser, newUser);
    return HttpResponse.json(newUser, { status: 201 });
  }),
];