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
import { ListaMaquinasComponent } from './pages/lista-maquinas/lista-maquinas.component';
import { CadastroMaquinaComponent } from './pages/cadastro-maquina/cadastro-maquina.component';
import { MaquinaUnicaComponent } from './pages/maquina-unica/maquina-unica.component';
import { CadastroRevisaoMaquinaComponent } from './pages/cadastro-revisao-maquina/cadastro-revisao-maquina.component';

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
  {
    path: 'dashboard',
    loadChildren: () => import('./pages/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  { path: 'maquinas', component: ListaMaquinasComponent, canActivate: [AuthGuard] },
  { path: 'maquinas/nova', component: CadastroMaquinaComponent, canActivate: [AuthGuard] },
  { path: 'maquinas/:id', component: MaquinaUnicaComponent, canActivate: [AuthGuard] },
  { path: 'manutencao/maquina', component: CadastroRevisaoMaquinaComponent, canActivate: [AuthGuard] },
  { path: 'login', component: AuthComponent },
  { path: 'registro', redirectTo: 'login' },
  { path: '**', redirectTo: 'carros' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
