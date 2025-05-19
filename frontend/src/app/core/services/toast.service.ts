// toast.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
    type: 'success' | 'error' | 'info' | 'warning';
    message: string;
    duration?: number;
}

@Injectable({
    providedIn: 'root',
})
export class ToastService {
    private _toasts = new BehaviorSubject<ToastMessage[]>([]);
    toasts$ = this._toasts.asObservable();

    show(toast: ToastMessage) {
        const currentToasts = this._toasts.getValue();
        this._toasts.next([...currentToasts, toast]);

        setTimeout(() => {
            this.remove(toast);
        }, toast.duration ?? 3000);
    }

    private remove(toast: ToastMessage) {
        const updatedToasts = this._toasts.getValue().filter(t => t !== toast);
        this._toasts.next(updatedToasts);
    }

    showSuccess(message: string, duration?: number) {
        this.show({ message, type: 'success', duration });
    }

    showError(message: string, duration?: number) {
        this.show({ message, type: 'error', duration });
    }

    showInfo(message: string, duration?: number) {
        this.show({ message, type: 'info', duration });
    }

    showWarning(message: string, duration?: number) {
        this.show({ message, type: 'warning', duration });
    }
}
