import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListMarcaComponent } from './list-marca.component';

describe('ListMarcaComponent', () => {
  let component: ListMarcaComponent;
  let fixture: ComponentFixture<ListMarcaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListMarcaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListMarcaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
