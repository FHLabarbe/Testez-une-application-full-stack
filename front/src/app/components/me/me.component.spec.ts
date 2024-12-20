import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { expect,jest } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { MeComponent } from './me.component';
import { User } from 'src/app/interfaces/user.interface';
import { of } from 'rxjs';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: jest.Mocked<UserService>;
  let router: jest.Mocked<Router>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn(),
  }
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch user information on init', () => {
    const mockUser: User = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    admin: false,
    password: 'mockPassword',
    createdAt: new Date('2024-12-19'),
    updatedAt: new Date('2024-12-20'),
  };
    const getByIdSpy = jest.spyOn(component['userService'], 'getById').mockReturnValue(of(mockUser));
    component.ngOnInit();
    expect(getByIdSpy).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(component.user).toEqual(mockUser);
  });

  it('should call back method when back button is clicked', () => {
    const backSpy = jest.spyOn(component, 'back');
    const backButton = fixture.nativeElement.querySelector('button[mat-icon-button]');
    backButton.click();
    expect(backSpy).toHaveBeenCalled();
  });

  it('should call delete method when delete button is clicked', () => {
    component.user = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      admin: false,
      password: 'mockPassword',
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    fixture.detectChanges();
    const deleteSpy = jest.spyOn(component, 'delete');
    const deleteButton = fixture.nativeElement.querySelector('button[mat-raised-button]');
    deleteButton.click();
    expect(deleteSpy).toHaveBeenCalled();
});




});
