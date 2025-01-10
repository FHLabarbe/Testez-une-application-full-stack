 describe('Session Tests - List Sessions', () => {
  beforeEach(() => {
    cy.visit('/');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
  });

  it('Should display the list of sessions', () => {
    cy.wait('@getSessions');
    cy.get('.items .item').should('have.length.greaterThan', 0);
  });
});
