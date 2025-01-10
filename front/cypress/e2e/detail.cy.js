describe('Session Detail', () => {

  it('Should open the detail page and show session/teacher info', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('login');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('GET', '/api/session/1', { fixture: 'session-admin.json' }).as('getSession');
    cy.intercept('GET', '/api/teacher/1', { fixture: 'teacher.json' }).as('getTeacher');

    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@login');
    cy.wait('@getSessions');

    cy.get('.items .item').first().contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');

    cy.wait('@getSession');
    cy.wait('@getTeacher');

    cy.get('h1').should('contain.text', 'Admin Session');
    cy.get('mat-card-subtitle').should('contain.text', 'John DOE');
    cy.get('.description').should('contain.text', 'Description: An admin scenario for testing delete');
  });

  it('Should allow an admin to delete the session', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('login');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('GET', '/api/session/1', { fixture: 'session-admin.json' }).as('getSession');
    cy.intercept('GET', '/api/teacher/1', { fixture: 'teacher.json' }).as('getTeacher');
    cy.intercept('DELETE', '/api/session/1', { statusCode: 200 }).as('deleteSession');

    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@login');
    cy.wait('@getSessions');

    cy.get('.items .item').first().contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');

    cy.wait('@getSession');
    cy.wait('@getTeacher');

    cy.get('button[mat-raised-button][color="warn"]').contains('Delete').click();
    cy.wait('@deleteSession');
    cy.get('.mat-snack-bar-container').should('contain.text', 'Session deleted !');
    cy.url().should('include', '/sessions');
  });

  it('Should handle back button', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('GET', '/api/session/1', { fixture: 'session.json' }).as('getSession');
    cy.intercept('GET', '/api/teacher/1', { fixture: 'teacher.json' }).as('getTeacher');

    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@login');
    cy.wait('@getSessions');

    cy.get('.items .item').first().contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');

    cy.wait('@getSession');
    cy.wait('@getTeacher');

    cy.get('button[mat-icon-button]').click();
    cy.url().should('not.include', '/sessions/detail/1');
  });
});
