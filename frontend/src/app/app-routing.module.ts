import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CadastroMarcaComponent } from './pages/cadastro-marca/cadastro-marca.component';
import { CadastroModeloComponent } from './pages/cadastro-modelo/cadastro-modelo.component';
import { CadastroCarroComponent } from './pages/cadastro-carro/cadastro-carro.component';
import { ListaCarrosComponent } from './pages/lista-carros/lista-carros.component';

const routes: Routes = [
  { path: 'marca', component: CadastroMarcaComponent },
  { path: 'modelo', component: CadastroModeloComponent },
  { path: 'carro', component: CadastroCarroComponent },
  { path: 'carros', component: ListaCarrosComponent },
  { path: '', redirectTo: 'carros', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
