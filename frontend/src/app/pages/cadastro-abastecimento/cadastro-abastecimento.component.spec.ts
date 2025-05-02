import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroAbastecimentoComponent } from './cadastro-abastecimento.component';

describe('CadastroAbastecimentoComponent', () => {
  let component: CadastroAbastecimentoComponent;
  let fixture: ComponentFixture<CadastroAbastecimentoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroAbastecimentoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroAbastecimentoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
