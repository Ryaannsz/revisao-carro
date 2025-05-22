import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingComponent } from './loading/loading.component';
import { ToastComponent } from './toast/toast.component';
import { ListMarcaComponent } from './shared-consult/list-marca/list-marca.component';
import { ListModelosComponent } from './shared-consult/list-modelos/list-modelos.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [LoadingComponent, ToastComponent, ListMarcaComponent, ListModelosComponent],
  imports: [CommonModule,
    MatFormFieldModule,
    MatAutocompleteModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
  ],
  exports: [LoadingComponent, ToastComponent, ListMarcaComponent, ListModelosComponent], // <- isso é essencial
})
export class SharedModule { }