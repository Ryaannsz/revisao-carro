import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarroUnicoComponent } from './carro-unico.component';

describe('CarroUnicoComponent', () => {
  let component: CarroUnicoComponent;
  let fixture: ComponentFixture<CarroUnicoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CarroUnicoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarroUnicoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
