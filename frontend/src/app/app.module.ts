import { DEFAULT_CURRENCY_CODE, LOCALE_ID, NgModule } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CadastrarModeloComponent } from './pages/cadastro-modelo/cadastro-modelo.component';
import { CadastrarCarroComponent } from './pages/cadastro-carro/cadastro-carro.component';
import { HeaderComponent } from './header/header.component';
import { CarroUnicoComponent } from './pages/carro-unico/carro-unico.component';
import { CadastroRevisaoComponent } from './pages/cadastro-revisao/cadastro-revisao.component';
import { RegistroRevisaoComponent } from './pages/registro-revisao/registro-revisao.component';
import { RegistroAbastecimentoComponent } from './pages/registro-abastecimento/registro-abastecimento.component';
import { LoadingComponent } from './shared/loading/loading.component';
import { SharedModule } from './shared/shared.module';
import { AuthComponent } from './auth/auth.component';
import { LoginComponent } from './auth/login/login.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { JwtModule } from '@auth0/angular-jwt';
import { CadastroMarcaComponent } from './pages/cadastro-marca/cadastro-marca.component';
import { CadastroAbastecimentoComponent } from './pages/cadastro-abastecimento/cadastro-abastecimento.component';
import { ListaCarrosComponent } from './pages/lista-carros/lista-carros.component';

import { ListMarcaComponent } from './shared/shared-consult/list-marca/list-marca.component';
import { ListModelosComponent } from './shared/shared-consult/list-modelos/list-modelos.component';

import { RegistroUsuarioComponent } from './pages/registro-usuario/registro-usuario.component';
import { ModalUsuarioComponent } from './modals/modal-usuario/modal-usuario.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ConfirmationDialogComponent } from './shared/confirmation-dialog/confirmation-dialog.component';
import { CadastroMaquinaComponent } from './pages/cadastro-maquina/cadastro-maquina.component';
import { ListaMaquinasComponent } from './pages/lista-maquinas/lista-maquinas.component';
import { MaquinaUnicaComponent } from './pages/maquina-unica/maquina-unica.component';
import { CadastroRevisaoMaquinaComponent } from './pages/cadastro-revisao-maquina/cadastro-revisao-maquina.component';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft, faBars, faCalendarAlt, faCalendarDay, faCar, faCarSide, faChartLine, faCheckCircle, faChevronDown, faChevronLeft,
  faChevronRight, faClock, faClockRotateLeft, faCoins, faEdit, faExpand, faFilePdf, faGasPump, faGear, faIdBadge, faInfoCircle,
  faLeaf, faPen, faPlus, faRightFromBracket, faRotate, faSave, faSearch, faTachometerAlt, faTimes, faTools, faTractor,
  faTrashAlt, faUserCircle, faUserPlus, faUsers, faWater, faWrench
} from '@fortawesome/free-solid-svg-icons';


registerLocaleData(localePt, 'pt-BR');

export function tokenGetter() {
  return localStorage.getItem('auth_token');
}

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    LoginComponent,
    RegistroComponent,
    CarroUnicoComponent,
    CadastroRevisaoComponent,
    RegistroRevisaoComponent,
    RegistroAbastecimentoComponent,
    AuthComponent,
    HeaderComponent,
    CadastroMarcaComponent,
    CadastroAbastecimentoComponent,
    CadastrarCarroComponent,
    CadastrarModeloComponent,
    ListaCarrosComponent,


    RegistroUsuarioComponent,
    ModalUsuarioComponent,
    ConfirmationDialogComponent,
    CadastroMaquinaComponent,
    ListaMaquinasComponent,
    MaquinaUnicaComponent,
    CadastroRevisaoMaquinaComponent,

  ],
  imports: [
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        //allowedDomains: ['http://revisao-carro.vercel.app'], // Domínios permitidos
        //disallowedRoutes: ['http://revisao-carro.vercel.app/login'] // Rotas sem token
        allowedDomains: ['http://localhost:8080'],
        disallowedRoutes: ['http://localhost:8080/login'],
      },
    }),
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    CommonModule,
    FormsModule,
    SharedModule,
    FontAwesomeModule,
    
    BrowserAnimationsModule,

  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: LOCALE_ID, useValue: 'pt-BR' },
    { provide: DEFAULT_CURRENCY_CODE, useValue: 'BRL' },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
    constructor(library: FaIconLibrary) {
    library.addIcons(faArrowLeft, faBars, faCalendarAlt, faCalendarDay, faCar, faCarSide, faChartLine, faCheckCircle,
      faChevronDown, faChevronLeft, faChevronRight, faClock, faClockRotateLeft, faCoins, faEdit, faExpand, faFilePdf, faGasPump,
      faGear, faIdBadge, faInfoCircle, faLeaf, faPen, faPlus, faRightFromBracket, faRotate, faSave, faSearch, faTachometerAlt,
      faTimes, faTools, faTractor, faTrashAlt, faUserCircle, faUserPlus, faUsers, faWater, faWrench);
  }
 }
