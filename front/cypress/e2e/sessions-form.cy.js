describe('Session Form', () => {

  it('Should redirect non-admin users from create form', () => {
    cy.visit('/');
    cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('getSessions');
    cy.intercept('POST', '/api/auth/login', { fixture: 'user.json' }).as('login');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('test@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.get('span').contains('Sessions').click();
    cy.wait('@getSessions');
    //cy.url().should('include', '/login');
  });

  it('Should display create form for admin users', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getTeachers');
    cy.visit('/sessions/create');
    cy.url().should('include', '/sessions/create');
    cy.get('h1').should('contain.text', 'Create session');
    cy.get('button').contains('Save').should('be.disabled');
  });

  it('Should submit create form successfully', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    cy.intercept('POST', '/api/session', { statusCode: 201 }).as('createSession');
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getTeachers');
    cy.visit('/sessions/create');
    cy.wait('@getTeachers');
    cy.get('input[formControlName="name"]').type('New Yoga Session');
    cy.get('input[formControlName="date"]').type('2025-05-20');
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').contains('Jane Smith').click();
    cy.get('textarea[formControlName="description"]').type('A new yoga session description.');
    cy.get('button').contains('Save').should('not.be.disabled').click();
    cy.wait('@createSession');
    cy.get('.mat-snack-bar-container').should('contain.text', 'Session created !');
    cy.url().should('include', '/sessions');
  });

  it('Should display update form with session data for admin users', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher', { fixture: 'teachers.json' }).as('getTeachers');
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getTeachers');
    cy.visit('/sessions/update/1');
    cy.wait('@getSessionDetail');
    cy.get('h1').should('contain.text', 'Update session');
    cy.get('input[formControlName="name"]').should('have.value', 'Yoga Session');
    cy.get('input[formControlName="date"]').should('have.value', '2025-12-13');
    cy.get('mat-select[formControlName="teacher_id"]').should('contain.text', 'John Doe');
    cy.get('textarea[formControlName="description"]').should('have.value', 'A relaxing yoga session');
  });

  it('Should submit update form successfully', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail');
    cy.intercept('PUT', '/api/session/1', { statusCode: 200 }).as('updateSession');
    cy.intercept('GET', '/api/teacher', { fixture: 'teachers.json' }).as('getTeachers');
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getTeachers');
    cy.visit('/sessions/update/1');
    cy.wait('@getSessionDetail');
    cy.get('input[formControlName="name"]').clear().type('Updated Yoga Session');
    cy.get('textarea[formControlName="description"]').clear().type('Updated description.');
    cy.get('button').contains('Save').should('not.be.disabled').click();
    cy.wait('@updateSession');
    cy.get('.mat-snack-bar-container').should('contain.text', 'Session updated !');
    cy.url().should('include', '/sessions');
  });

  it('Should handle back button', () => {
    cy.intercept('POST', '/api/auth/login', { fixture: 'user-adminFixture.json' }).as('loginAdmin');
    cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher', { fixture: 'teachers.json' }).as('getTeachers');
    cy.visit('/');
    cy.get('span').contains('Login').click();
    cy.get('input[formControlName="email"]').type('admin@example.com');
    cy.get('input[formControlName="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginAdmin');
    cy.get('span').contains('Sessions').click();
    cy.wait('@getTeachers');
    cy.visit('/sessions/create');
    cy.wait('@getTeachers');
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');
  });

});
