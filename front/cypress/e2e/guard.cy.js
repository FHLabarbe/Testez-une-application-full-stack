/// <reference types="cypress" />

describe('Guards Tests', () => {
  
  beforeEach(() => {
    cy.visit('/');
  });

  it('Should redirect a non-logged user to login when accessing a protected route', () => {
    cy.visit('/sessions');
    cy.url().should('include', '/login');
  });

  it('Should allow a logged user to access a protected route', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.wait('@getSessions');
    cy.url().should('include', '/sessions');
  });

  /*it('Should redirect a logged user away from login or register pages', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.wait('@getSessions')
    cy.url().should('include', '/sessions');
  });*/
});
