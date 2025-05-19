import { Component } from '@angular/core';
import { ToastService } from '../../core/services/toast.service';

@Component({
    standalone: false,
    selector: 'app-toast',
    templateUrl: './toast.component.html'
})
export class ToastComponent {
    constructor(public toastService: ToastService) { }
}
