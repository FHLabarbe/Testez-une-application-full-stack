import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { Router } from '@angular/router';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  } 

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule, 
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with the correct controls', () => {
    const form = component.sessionForm;
    expect(form).toBeTruthy();
    expect(form?.get('name')).toBeTruthy();
    expect(form?.get('name')?.hasValidator(Validators.required)).toBe(true);
    expect(form?.get('date')).toBeTruthy();
    expect(form?.get('date')?.hasValidator(Validators.required)).toBe(true);
    expect(form?.get('teacher_id')).toBeTruthy();
    expect(form?.get('teacher_id')?.hasValidator(Validators.required)).toBe(true);
    expect(form?.get('description')).toBeTruthy();
    expect(form?.get('description')?.hasValidator(Validators.required)).toBe(true);
  });

  // We are mocking the animation to solve the js error when testing update
  it('should mock animate correctly', () => {
    const element = document.createElement('div');
    element.animate([{ opacity: 0 }, { opacity: 1 }], { duration: 1000 });
    expect(element.animate).toBeDefined();
    expect(element.animate).toHaveBeenCalled();
  });

  it('should create a session and then update it', () => {
    const sessionApiService = TestBed.inject(SessionApiService);
    const createSpy = jest.spyOn(sessionApiService, 'create').mockReturnValueOnce({
      subscribe: jest.fn((callback) => callback({ id: '1' })),
    } as any);
    const updateSpy = jest.spyOn(sessionApiService, 'update').mockReturnValueOnce({
      subscribe: jest.fn(),
    } as any);
    component.onUpdate = false;
    component.sessionForm?.setValue({
      name: 'Initial Session',
      date: '2024-12-20',
      teacher_id: 1,
      description: 'Initial description',
    });
    component.submit();
    expect(createSpy).toHaveBeenCalledWith({
      name: 'Initial Session',
      date: '2024-12-20',
      teacher_id: 1,
      description: 'Initial description',
    });
    component.onUpdate = true;
    (component as any).id = '1'; // the setter is missing in the component.ts
    component.sessionForm?.setValue({
      name: 'Updated Session',
      date: '2024-12-25',
      teacher_id: 2,
      description: 'Updated description',
    });
    component.submit();
    expect(updateSpy).toHaveBeenCalledWith('1', {
      name: 'Updated Session',
      date: '2024-12-25',
      teacher_id: 2,
      description: 'Updated description',
    });
  });

  it('should redirect non-admin users to sessions page', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = jest.spyOn(router, 'navigate');
    (component as any).sessionService.sessionInformation.admin = false;
    component.ngOnInit();
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });


  it('should display correct title based on onUpdate state', () => {
    component.onUpdate = false;
    fixture.detectChanges();
    const createTitle = fixture.nativeElement.querySelector('h1');
    expect(createTitle.textContent).toContain('Create session');
    component.onUpdate = true;
    fixture.detectChanges();
    const updateTitle = fixture.nativeElement.querySelector('h1');
    expect(updateTitle.textContent).toContain('Update session');
  });

  it('should call snackbar and navigate on exitPage', () => {
    const router = TestBed.inject(Router);
    const snackBar = TestBed.inject(MatSnackBar);
    const snackBarSpy = jest.spyOn(snackBar, 'open');
    const navigateSpy = jest.spyOn(router, 'navigate');
    component['exitPage']('Exit message');
    expect(snackBarSpy).toHaveBeenCalledWith('Exit message', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should initialize form for creation', () => {
    component.onUpdate = false;
    component['initForm']();
    const form = component.sessionForm;

    expect(form?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: '',
    });
  });

  it('should initialize form for update', () => {
    const session: Session = {
      id: 1,
      name: 'Updated Session',
      date: new Date('2024-12-25'),
      teacher_id: 2,
      description: 'Updated description',
      createdAt: new Date(),
      updatedAt: new Date(),
      users: [],
    };
    component.onUpdate = true;
    component['initForm'](session);
    expect(component.sessionForm?.value).toEqual({
      name: 'Updated Session',
      date: '2024-12-25',
      teacher_id: 2,
      description: 'Updated description',
    });
  });

  it('should disable save button if form is invalid', () => {
    const saveButton = fixture.nativeElement.querySelector('button[type="submit"]');
    component.sessionForm?.setValue({
      name: '',
      date: '',
      teacher_id: '',
      description: '',
    });
    fixture.detectChanges();
    expect(saveButton.disabled).toBe(true);
    component.sessionForm?.setValue({
      name: 'Valid Session',
      date: '2024-12-25',
      teacher_id: 1,
      description: 'Valid description',
    });
    fixture.detectChanges();
    expect(saveButton.disabled).toBe(false);
  });


});
