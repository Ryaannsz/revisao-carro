import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroMarcaComponent } from './cadastro-marca.component';

describe('CadastroMarcaComponent', () => {
  let component: CadastroMarcaComponent;
  let fixture: ComponentFixture<CadastroMarcaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CadastroMarcaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroMarcaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
