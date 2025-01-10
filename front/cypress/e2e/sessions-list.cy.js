describe('Sessions List E2E', () => {
  beforeEach(() => {
    cy.visit('/');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.get('span').contains('Sessions').click();
    cy.wait('@getSessions');
  });

  it('Should display the sessions list for a non-admin user', () => {
    cy.get('.items .item').should('have.length.greaterThan', 0);
    cy.get('button[routerLink="create"]').should('not.exist');
    cy.get('.items .item').each(($item) => {
      cy.wrap($item).contains('Detail').should('exist');
      cy.wrap($item).contains('Edit').should('not.exist');
    });
  });

  it('Should display the sessions list for an admin user', () => {
    cy.reload();
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-admin.json' }).as('loginAdmin');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.get('span').contains('Sessions').click();
    cy.wait('@getSessions');
    cy.get('.items .item').should('have.length.greaterThan', 0);
    cy.get('button[routerLink="create"]').should('exist');
    cy.get('.items .item').each(($item) => {
      cy.wrap($item).contains('Detail').should('exist');
      cy.wrap($item).contains('Edit').should('exist');
    });
  });

  it('Should open a detail for the first item', () => {
    cy.intercept('GET', '/api/session/1', { fixture: 'session.json' }).as('getSessionDetail');
    cy.get('.items .item').first().contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');
    cy.wait('@getSessionDetail');
  });
});
