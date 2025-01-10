/// <reference types="cypress" />

describe('Authentication Tests', () => {
  
  beforeEach(() => {
    cy.visit('/');
  });

  it('Should display the home page by default', () => {
    cy.url().should('include', '/');
  });

  it('Should navigate to the login page and display its structure', () => {
    cy.get('span').contains('Login').click();
    cy.url().should('include', '/login');
    cy.get('mat-card-title').should('contain.text', 'Login');
    cy.get('form.login-form').should('exist');
  });

  it('Should allow the user to log in with valid credentials', () => {
    cy.get('span').contains('Login').click();
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.url().should('include', '/sessions');
    cy.get('span').should('contain.text', 'Logout');
  });

  it('Should show an error for invalid credentials on login', () => {
    cy.get('span').contains('Login').click();
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid email or password' },
    }).as('loginFail');
    cy.get('input[formControlName="email"]').type('invalid@example.com');
    cy.get('input[formControlName="password"]').type('wrongpassword');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginFail');
    cy.get('.error').should('contain.text', 'An error occurred');
  });

  it('Should navigate to the register page and display its structure', () => {
    cy.get('span').contains('Register').click();
    cy.url().should('include', '/register');
    cy.get('mat-card-title').should('contain.text', 'Register');
    cy.get('form.register-form').should('exist');
  });

  it('Should allow the user to register with valid data', () => {
    cy.get('span').contains('Register').click();
    cy.intercept('POST', '/api/auth/register', { statusCode: 201 }).as('register');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@register').its('response.statusCode').should('eq', 201);
    cy.url().should('include', '/login');
    cy.get('.error').should('not.exist');
  });

  it('Should show an error for invalid data on register', () => {
    cy.get('span').contains('Register').click();
    cy.intercept('POST', '/api/auth/register', { statusCode: 400 }).as('registerFail');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('test@exampl');
    cy.get('input[formControlName="password"]').type('password123');  
    cy.get('button[type="submit"]').click();
    cy.wait('@registerFail');
    cy.get('.error').should('exist').and('contain.text', 'An error occurred');
  });

  it('Should log the user out and redirect to home', () => {
    cy.get('span').contains('Login').click();
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.get('span').contains('Logout').click(); 
    cy.url().should('include', '/');
    cy.get('span').should('contain.text', 'Login');
  });

  it('Should initialize the register form with correct controls', () => {
    cy.get('span').contains('Register').click();
    cy.get('input[formControlName="firstName"]').should('exist').and('have.value', '');
    cy.get('input[formControlName="lastName"]').should('exist').and('have.value', '');
    cy.get('input[formControlName="email"]').should('exist').and('have.value', '');
    cy.get('input[formControlName="password"]').should('exist').and('have.value', '');
  });

});
