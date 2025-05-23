import { abastHandlers } from './abast.handler'
import { carroHandlers } from './carro.handler'
import { marcaHandlers } from './marca.handler'
import { modeloHandlers } from './modelo.handler'
import { revisaoHandlers } from './revisao.handler'


export const Handlers = [
    ...carroHandlers,
    ...abastHandlers,
    ...marcaHandlers,
    ...modeloHandlers,
    ...revisaoHandlers
]