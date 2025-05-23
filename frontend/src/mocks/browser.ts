import { setupWorker } from 'msw/browser';
import { Handlers } from './handlers/handlers';

export const worker = setupWorker(...Handlers);

