import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
    type: 'sucesso' | 'erro' | 'info' | 'aviso';
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
            this.removeToast(toast);
        }, toast.duration ?? 3000);
    }

    // Método público para remover o toast, usado pelo componente para fechar manualmente
    removeToast(toast: ToastMessage) {
        const updatedToasts = this._toasts.getValue().filter(t => t !== toast);
        this._toasts.next(updatedToasts);
    }

    showSuccess(message: string, duration?: number) {
        this.show({ message, type: 'sucesso', duration });
    }

    showError(message: string, duration?: number) {
        this.show({ message, type: 'erro', duration });
    }

    showInfo(message: string, duration?: number) {
        this.show({ message, type: 'info', duration });
    }

    showWarning(message: string, duration?: number) {
        this.show({ message, type: 'aviso', duration });
    }
}
