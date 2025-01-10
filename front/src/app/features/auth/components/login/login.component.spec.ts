import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of, throwError } from 'rxjs';
import { LoginRequest } from '../../interfaces/loginRequest.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  const mockAuthService = {
    login: jest.fn(),
  };

  const mockSessionService = {
    logIn: jest.fn(),
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call authService.login and sessionService.logIn on successful submit', () => {
    const mockResponse: SessionInformation = {
      id: 1,
      username: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: true,
      token: 'mock-token',
      type: 'Bearer',
    };

    const navigateSpy = jest.spyOn(router, 'navigate');
    mockAuthService.login.mockReturnValue(of(mockResponse));

    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123',
    } as LoginRequest);
    expect(sessionService.logIn).toHaveBeenCalledWith(mockResponse);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onError to true on failed submit', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

    component.form.setValue({
      email: 'wrong@example.com',
      password: 'wrongpassword',
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'wrong@example.com',
      password: 'wrongpassword',
    } as LoginRequest);
    expect(component.onError).toBe(true);
  });

  it('should keep onError as false on successful submit', () => {
    const mockResponse: SessionInformation = {
      id: 1,
      username: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: true,
      token: 'mock-token',
      type: 'Bearer',
    };

    mockAuthService.login.mockReturnValue(of(mockResponse));

    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });

    component.submit();

    expect(component.onError).toBe(false);
  });

  it('should disable the submit button if the form is invalid', () => {
    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    component.form.setValue({
      email: '',
      password: '',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBe(true);

    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBe(false);
  });
});
