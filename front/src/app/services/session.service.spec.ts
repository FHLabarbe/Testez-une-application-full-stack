import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
      id: 1,
      username: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: true,
      token: 'bearer',
      type: 'type',
    };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with isLogged as false and sessionInformation as undefined', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit the initial value of isLoggedSubject as false', (done) => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(false);
      done();
    });
  });

  it('should update sessionInformation and isLogged on login', () => {
    service.logIn(mockUser);

    expect(service.sessionInformation).toEqual(mockUser);
    expect(service.isLogged).toBe(true);
  });

  it('should emit true when login is called', (done) => {
    service.$isLogged().subscribe((isLogged) => {
      if (isLogged) {
        expect(isLogged).toBe(true);
        done();
      }
    });
    service.logIn(mockUser);
  });

  it('should reset sessionInformation and set isLogged to false on logout', () => {
    service.logIn(mockUser);
    service.logOut();
    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
  });

  it('should emit false when logout is called', (done) => {
    service.logIn(mockUser);
    service.$isLogged().subscribe((isLogged) => {
      if (!isLogged) {
        expect(isLogged).toBe(false);
        done();
      }
    });
    service.logOut();
  });
});
