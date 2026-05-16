/**
 * E2E: Trang 404 (NotFoundView)
 */

describe('Trang 404', () => {
  it('truy cập URL không tồn tại → hiển thị 404', () => {
    cy.visit('/duong-dan-khong-hop-le', { failOnStatusCode: false })
    cy.contains('404').should('be.visible')
    cy.contains('Trang không tồn tại').should('be.visible')
  })

  it('click "Về trang chủ" → quay về /', () => {
    cy.visit('/xyz-abc-123', { failOnStatusCode: false })
    cy.contains('button', 'Về trang chủ').click()
    cy.url().should('match', /\/$/)
    cy.contains('Giao Đồ Ăn').should('be.visible')
  })
})
