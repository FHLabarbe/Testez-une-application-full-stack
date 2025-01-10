describe('Account / me test', () => {
  beforeEach(() => {
    cy.visit('/');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@getSessions');
  });

  it('Should access to /me and display user-admin information then back', () => {
    cy.intercept('GET', '/api/user/1', { fixture: 'user-admin.json' }).as('getUser');
    cy.get('span').contains('Account').click();
    cy.url().should('include', '/me');
    cy.wait('@getUser');
    cy.get('mat-card-content p').contains('Name: John DOE');
    cy.get('mat-card-content p').contains('Email: test@example.com');
    cy.get('.my2').should('contain.text', 'You are admin');
    cy.get('mat-card-content p').contains('Create at: January 1, 1970');
    cy.get('mat-card-content p').contains('Last update: January 1, 1970');
    cy.get('button[mat-icon-button]').click();
    cy.url().should('not.include', '/me');
  });

  it('Should delete my account', () => {
    cy.intercept('GET', '/api/user/1', { fixture: 'user.json' }).as('getUser');
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
      body: null,
    }).as('deleteUser');
    cy.get('span').contains('Account').click();
    cy.wait('@getUser');
    cy.get('.my2').should('contain.text','Delete my account:');
    cy.get('button[mat-raised-button][color="warn"]').click();
    cy.wait('@deleteUser').its('response.statusCode').should('eq', 200);

    cy.get('.mat-snack-bar-container').should('contain.text', 'Your account has been deleted !');
    cy.url().should('include', '/');
  });
});