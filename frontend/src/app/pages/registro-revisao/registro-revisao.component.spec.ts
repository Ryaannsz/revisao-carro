import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistroRevisaoComponent } from './registro-revisao.component';

describe('RegistroRevisaoComponent', () => {
  let component: RegistroRevisaoComponent;
  let fixture: ComponentFixture<RegistroRevisaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistroRevisaoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistroRevisaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
