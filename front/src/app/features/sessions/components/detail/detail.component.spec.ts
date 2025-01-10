import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  const mockSession = {
    id: 1,
    name: 'Yoga Session',
    description: 'A yoga session',
    date: new Date(),
    createdAt: new Date(),
    updatedAt: new Date(),
    teacher_id: 1,
    users: [],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent], 
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    })
      .compileComponents();
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    component.session = { ...mockSession };
    component.isAdmin = mockSessionService.sessionInformation.admin;
    component.isParticipate = false;
    fixture.detectChanges();
  });

  // Méthode pour intéragir avec les boutons
  const getButton = (selector: string) =>
    fixture.nativeElement.querySelector(selector);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call back method when back button is clicked', () => {
    const backSpy = jest.spyOn(component, 'back');
    const backButton = getButton('button[mat-icon-button]');
    backButton.click();
    expect(backSpy).toHaveBeenCalled();
  });

  it('should display the session name', () => {
    const sessionNameElement = getButton('h1');
    expect(sessionNameElement.textContent).toContain('Yoga Session');
  });

  it('should call delete method when delete button is clicked', () => {
    component.isAdmin = true;
    fixture.detectChanges();
    const deleteSpy = jest.spyOn(component, 'delete');
    const deleteButton = getButton('button[color="warn"]');
    deleteButton.click();
    expect(deleteSpy).toHaveBeenCalled();
  });

  it('should call participate method when participate button is clicked', () => {
    component.isAdmin = false;
    component.isParticipate = false;
    fixture.detectChanges();
    const participateSpy = jest.spyOn(component, 'participate');
    const participateButton = getButton('button[color="primary"]');
    participateButton.click();
    expect(participateSpy).toHaveBeenCalled();
  });

  it('should call unParticipate method when do not participate button is clicked', () => {
    component.isAdmin = false;
    component.isParticipate = true;
    fixture.detectChanges();
    const unParticipateSpy = jest.spyOn(component, 'unParticipate');
    const unParticipateButton = getButton('button[color="warn"]');
    unParticipateButton.click();
    expect(unParticipateSpy).toHaveBeenCalled();
  });

});

