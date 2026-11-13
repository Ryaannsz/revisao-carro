import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LoadingComponent } from './loading/loading.component';
import { ToastComponent } from './toast/toast.component';
import { ListMarcaComponent } from './shared-consult/list-marca/list-marca.component';
import { ListModelosComponent } from './shared-consult/list-modelos/list-modelos.component';
import { MedidorComponent } from './medidor/medidor.component';
import { SeletorComponent } from './seletor/seletor.component';

@NgModule({
  declarations: [LoadingComponent, ToastComponent, ListMarcaComponent, ListModelosComponent, MedidorComponent, SeletorComponent],
  imports: [CommonModule, ReactiveFormsModule, FormsModule, FontAwesomeModule],
  exports: [LoadingComponent, ToastComponent, ListMarcaComponent, ListModelosComponent, MedidorComponent, SeletorComponent],
})
export class SharedModule { }
