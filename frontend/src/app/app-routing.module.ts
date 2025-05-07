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

const routes: Routes = [
  { path: 'marca', component: CadastroMarcaComponent },
  { path: 'modelo', component: CadastrarModeloComponent },
  { path: 'carro', component: CadastrarCarroComponent },
  { path: 'carros', component: ListaCarrosComponent },
  { path: 'abast', component: CadastroAbastecimentoComponent},
  { path: '', redirectTo: 'carros', pathMatch: 'full' },
  {path: 'carros/unico/:id', component: CarroUnicoComponent},
  {path: 'revisao/cadastrar', component: CadastroRevisaoComponent},
  {path: 'abastecimento/registros', component: RegistroAbastecimentoComponent},
  {path: 'revisao/registros', component: RegistroRevisaoComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
