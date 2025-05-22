import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { ConfirmationDialogComponent } from "../../shared/confirmation-dialog/confirmation-dialog.component";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})

export class ConfirmationDialogService {
    constructor(private dialog: MatDialog) { }

    confirm(title: string, message: string): Observable<boolean> {
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
            data: { title, message },
            disableClose: true,
            backdropClass: 'custom-backdrop',
            panelClass: 'custom-panel'
        });

        return dialogRef.afterClosed();
    }
}