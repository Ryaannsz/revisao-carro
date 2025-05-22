import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CadastrarModeloComponent } from './pages/cadastro-modelo/cadastro-modelo.component';
import { CadastrarCarroComponent } from './pages/cadastro-carro/cadastro-carro.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { RegistroUsuarioComponent } from './pages/registro-usuario/registro-usuario.component';
import { ModalUsuarioComponent } from './modals/modal-usuario/modal-usuario.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ConfirmationDialogComponent } from './shared/confirmation-dialog/confirmation-dialog.component';


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
    FooterComponent,
    CadastroMarcaComponent,
    CadastroAbastecimentoComponent,
    CadastrarCarroComponent,
    CadastrarModeloComponent,
    ListaCarrosComponent,


    RegistroUsuarioComponent,
    ModalUsuarioComponent,
    ConfirmationDialogComponent,

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

    MatFormFieldModule,
    MatAutocompleteModule,
    MatInputModule,
    MatButtonModule,

    BrowserAnimationsModule,

  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule { }
