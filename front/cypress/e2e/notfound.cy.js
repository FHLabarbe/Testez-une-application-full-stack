describe('NotFoundComponent', () => {
    it('Should print 404 when page does not exist', () => {
      cy.visit('/route-inexistante');
      cy.contains('Page not found !').should('be.visible');
      cy.get('h1').should('contain.text', 'Page not found !');
      cy.url().should('include', '/404');
    });
  });