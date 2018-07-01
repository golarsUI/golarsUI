import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MiddlepaneComponent } from './middlepane.component';

describe('MiddlepaneComponent', () => {
  let component: MiddlepaneComponent;
  let fixture: ComponentFixture<MiddlepaneComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MiddlepaneComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MiddlepaneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
