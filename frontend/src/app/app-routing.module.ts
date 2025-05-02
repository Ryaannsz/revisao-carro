import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CadastroMarcaComponent } from './pages/cadastro-marca/cadastro-marca.component';
import { CadastrarCarroComponent } from './pages/cadastro-carro/cadastro-carro.component';
import { CadastrarModeloComponent } from './pages/cadastro-modelo/cadastro-modelo.component';
import { ListaCarrosComponent } from './pages/lista-carros/lista-carros.component';
import { CadastroAbastecimentoComponent } from './pages/cadastro-abastecimento/cadastro-abastecimento.component';

const routes: Routes = [
  { path: 'marca', component: CadastroMarcaComponent },
  { path: 'modelo', component: CadastrarModeloComponent },
  { path: 'carro', component: CadastrarCarroComponent },
  { path: 'carros', component: ListaCarrosComponent },
  { path: 'abast', component: CadastroAbastecimentoComponent},
  { path: '', redirectTo: 'carros', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
