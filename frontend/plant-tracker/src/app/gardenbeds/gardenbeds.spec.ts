import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Gardenbeds } from './gardenbeds';

describe('Gardenbeds', () => {
  let component: Gardenbeds;
  let fixture: ComponentFixture<Gardenbeds>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Gardenbeds],
    }).compileComponents();

    fixture = TestBed.createComponent(Gardenbeds);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
