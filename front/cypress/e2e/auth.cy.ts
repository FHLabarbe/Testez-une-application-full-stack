describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login'); // Naviguer vers la page de connexion avant chaque test
  });

  it('Login successful', () => {
    // Intercepter la requête POST pour l'authentification
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    }).as('loginRequest');

    // Intercepter la requête GET pour récupérer les sessions
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('fetchSessions');

    // Entrer les informations de connexion
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}');

    // Vérifier que l'URL contient "/sessions"
    cy.url().should('include', '/sessions');

    // Vérifier que les requêtes ont bien été effectuées
    cy.wait('@loginRequest').its('request.body').should('deep.equal', {
      email: 'yoga@studio.com',
      password: 'test!1234',
    });
    cy.wait('@fetchSessions');

    // Vérifier des éléments spécifiques après connexion
    cy.contains('Sessions').should('be.visible');
    cy.contains('Account').should('be.visible');
    cy.contains('Logout').should('be.visible');
  });
});
