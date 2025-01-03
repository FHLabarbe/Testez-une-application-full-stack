/// <reference types="cypress" />

describe('Authentication Tests', () => {
  
  beforeEach(() => {
    cy.visit('/');
  });

  it('Should display the home page by default', () => {
    cy.url().should('include', '/'); // Vérifie que la page d'accueil est bien affichée
  });

  it('Should navigate to the login page and display its structure', () => {
    cy.get('span').contains('Login').click(); // Cliquer sur le bouton Login
    cy.url().should('include', '/login'); // Vérifie que l'URL est correcte
    cy.get('mat-card-title').should('contain.text', 'Login'); // Vérifie le titre de la page
    cy.get('form.login-form').should('exist'); // Vérifie la présence du formulaire
  });

  it('Should allow the user to log in with valid credentials', () => {
    cy.get('span').contains('Login').click(); // Aller sur la page Login
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.url().should('include', '/sessions'); // Vérifie la redirection vers sessions
    cy.get('span').should('contain.text', 'Logout'); // Vérifie que le bouton Logout est visible
  });

  it('Should show an error for invalid credentials on login', () => {
    cy.get('span').contains('Login').click(); // Aller sur la page Login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid email or password' },
    }).as('loginFail');
    cy.get('input[formControlName="email"]').type('invalid@example.com');
    cy.get('input[formControlName="password"]').type('wrongpassword');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginFail');
    cy.get('.error').should('contain.text', 'An error occurred'); // Vérifie le message d'erreur
  });

  it('Should navigate to the register page and display its structure', () => {
    cy.get('span').contains('Register').click(); // Cliquer sur Register
    cy.url().should('include', '/register'); // Vérifie l'URL
    cy.get('mat-card-title').should('contain.text', 'Register'); // Vérifie le titre de la page
    cy.get('form.register-form').should('exist'); // Vérifie la présence du formulaire
  });

  it('Should allow the user to register with valid data', () => {
    cy.get('span').contains('Register').click(); // Aller sur la page Register
    cy.intercept('POST', '/api/auth/register', { statusCode: 201 }).as('register');
    cy.get('input[formControlName="firstName"]').type('John');
    cy.get('input[formControlName="lastName"]').type('Doe');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@register');
    cy.url().should('include', '/login'); // Vérifie la redirection vers login
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
    cy.get('.error').should('contain.text', 'An error occurred');
  });

  it('Should log the user out and redirect to home', () => {
    cy.get('span').contains('Login').click(); // Aller sur la page Login
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');
    cy.get('span').contains('Logout').click(); // Cliquer sur Logout
    cy.url().should('include', '/'); // Vérifie le retour à la page d'accueil
    cy.get('span').should('contain.text', 'Login'); // Vérifie que le bouton Login est visible
  });
});
