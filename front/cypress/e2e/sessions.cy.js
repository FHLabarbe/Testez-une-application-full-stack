 describe('Session Tests - List Sessions', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('GET', '/api/auth/user', {
      body: {
        id: 1,
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
      },
    }).as('getUser');
  });

  it('Should display the list of sessions', () => {
    cy.visit('/sessions');
    cy.wait('@getSessions').then((interception) => {
      expect(interception.response.statusCode).to.eq(200);
    });

    cy.get('.items .item').should('have.length.greaterThan', 0);
  });
});
