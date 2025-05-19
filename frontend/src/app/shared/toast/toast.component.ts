import { Component } from '@angular/core';
import { ToastService, ToastMessage } from '../../core/services/toast.service';
import { Observable } from 'rxjs';

@Component({
    standalone: false,
    selector: 'app-toast',
    templateUrl: './toast.component.html',
    styleUrls: ['./toast.component.css']
})
export class ToastComponent {
    toasts$: Observable<ToastMessage[]>;

    constructor(private toastService: ToastService) {
        this.toasts$ = this.toastService.toasts$;
    }

    close(toast: ToastMessage) {
        this.toastService.removeToast(toast);
    }
}
