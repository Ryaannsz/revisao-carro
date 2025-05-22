import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: false,
  templateUrl: './confirmation-dialog.component.html',
  styleUrl: './confirmation-dialog.component.css'
})
export class ConfirmationDialogComponent implements OnInit {
  isOpen = false;

  constructor(
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      title: string;
      message: string;
    }
  ) { }

  ngOnInit(): void {
    // Ativa a animação após um pequeno delay para garantir a renderização
    setTimeout(() => this.isOpen = true, 10);
  }

  confirm(): void {
    this.isOpen = false;
    // Delay para permitir a animação de saída antes de fechar
    setTimeout(() => this.dialogRef.close(true), 300);
  }

  cancel(): void {
    this.isOpen = false;
    // Delay para permitir a animação de saída antes de fechar
    setTimeout(() => this.dialogRef.close(false), 300);
  }
}
