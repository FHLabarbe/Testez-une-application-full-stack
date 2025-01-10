import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';

import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: jest.Mocked<UserService>;
  let router: jest.Mocked<Router>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut: jest.fn(),
  };

  beforeEach(async () => {
    const mockRouter = {
      navigate: jest.fn(),
    };

    const mockMatSnackBar = {
      open: jest.fn(),
    };

    const mockUserService = {
      getById: jest.fn().mockReturnValue(of({
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        admin: false,
        password: 'mockPassword',
        createdAt: new Date(),
        updatedAt: new Date(),
      })),
      delete: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [MatSnackBarModule, HttpClientModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: UserService, useValue: mockUserService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch user information on init', () => {
    component.ngOnInit();
    expect(userService.getById).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(component.user).toEqual({
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      admin: false,
      password: 'mockPassword',
      createdAt: expect.any(Date),
      updatedAt: expect.any(Date),
    });
  });

  it('should call back method when back button is clicked', () => {
    const backSpy = jest.spyOn(component, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
  });

  it('should delete user account and display snackbar message', () => {
    component.delete();
    expect(userService.delete).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(matSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should call delete method when delete button is clicked', () => {
    const deleteSpy = jest.spyOn(component, 'delete');
    const deleteButton = fixture.nativeElement.querySelector('button[mat-raised-button]');
    deleteButton.click();
    expect(deleteSpy).toHaveBeenCalled();
  });
});