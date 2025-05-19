import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingComponent } from './loading/loading.component';
import { ToastComponent } from './toast/toast.component';

@NgModule({
  declarations: [LoadingComponent, ToastComponent],
  imports: [CommonModule],
  exports: [LoadingComponent, ToastComponent], // <- isso é essencial
})
export class SharedModule { }