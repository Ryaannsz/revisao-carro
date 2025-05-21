import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CadastroMarcaComponent } from './pages/cadastro-marca/cadastro-marca.component';
import { CadastrarCarroComponent } from './pages/cadastro-carro/cadastro-carro.component';
import { CadastrarModeloComponent } from './pages/cadastro-modelo/cadastro-modelo.component';
import { ListaCarrosComponent } from './pages/lista-carros/lista-carros.component';
import { CadastroAbastecimentoComponent } from './pages/cadastro-abastecimento/cadastro-abastecimento.component';
import { CarroUnicoComponent } from './pages/carro-unico/carro-unico.component';
import { CadastroRevisaoComponent } from './pages/cadastro-revisao/cadastro-revisao.component';
import { RegistroAbastecimentoComponent } from './pages/registro-abastecimento/registro-abastecimento.component';
import { RegistroRevisaoComponent } from './pages/registro-revisao/registro-revisao.component';
import { AuthComponent } from './auth/auth.component';
import { AuthGuard } from './guards/AuthGuard';
import { RegistroUsuarioComponent } from './pages/registro-usuario/registro-usuario.component';

const routes: Routes = [
  {
    path: 'marca',
    component: CadastroMarcaComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'modelo',
    component: CadastrarModeloComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'carro',
    component: CadastrarCarroComponent,
    canActivate: [AuthGuard],
  },
  { path: 'carros', component: ListaCarrosComponent, canActivate: [AuthGuard] },
  {
    path: 'abast',
    component: CadastroAbastecimentoComponent,
    canActivate: [AuthGuard],
  },
  { path: '', redirectTo: 'carros', pathMatch: 'full' },
  {
    path: 'carros/unico/:id',
    component: CarroUnicoComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'revisao/cadastrar',
    component: CadastroRevisaoComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'abastecimento/registros',
    component: RegistroAbastecimentoComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'revisao/registros',
    component: RegistroRevisaoComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'usuario/cadastrados',
    component: RegistroUsuarioComponent,
    canActivate: [AuthGuard],
  },
  { path: 'login', component: AuthComponent },
  { path: 'registro', component: AuthComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
