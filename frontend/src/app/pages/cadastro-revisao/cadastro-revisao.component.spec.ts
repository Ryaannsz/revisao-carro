import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroRevisaoComponent } from './cadastro-revisao.component';

describe('CadastroRevisaoComponent', () => {
  let component: CadastroRevisaoComponent;
  let fixture: ComponentFixture<CadastroRevisaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CadastroRevisaoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroRevisaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
