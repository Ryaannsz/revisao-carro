export class AppConstants {
  static readonly BASE_URL = 'https://revisao-carro.onrender.com';
  //static readonly BASE_URL = 'http://localhost:8080'

  static readonly MARCA_URL = `${AppConstants.BASE_URL}/marca`;
  static readonly MODELO_URL = `${AppConstants.BASE_URL}/modelo`;
  static readonly CARRO_URL = `${AppConstants.BASE_URL}/carro`;
  static readonly ABAST_URL = `${AppConstants.BASE_URL}/abastecimento`;
  static readonly REVISAO_URL = `${AppConstants.BASE_URL}/revisao`;
  static readonly USER_URL = `${AppConstants.BASE_URL}/user`;

  static readonly LOGIN_URL = `${AppConstants.BASE_URL}/auth/login`;
  static readonly REGISTRO_URL = `${AppConstants.BASE_URL}/auth/registro`;
}
