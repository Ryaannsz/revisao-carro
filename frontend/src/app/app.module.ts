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


@NgModule({
  declarations: [
    
  
    CarroUnicoComponent,
              CadastroRevisaoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    CommonModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
