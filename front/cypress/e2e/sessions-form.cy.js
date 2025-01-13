describe('Session Form', () => {

  it('Should redirect non-admin users from create form', () => {
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    
    cy.wait('@login');
    cy.wait('@getSessions');
    
    cy.get('span').contains('Sessions').click();
    
    cy.visit('/sessions/create');
    cy.url().should('include', '/login');
  });

  it('Should display create form for admin users', () => {
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getSessions');

    cy.contains('button', 'Create').click();
    cy.url().should('include', '/sessions/create');
    cy.contains('button', 'Save').should('be.disabled');
  });

});
