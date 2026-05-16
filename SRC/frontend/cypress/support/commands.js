/**
 * Lệnh Cypress dùng chung cho E2E frontend (luồng khách hàng).
 */

const MOCK_CUSTOMER = {
  id: 1,
  email: 'customer@test.com',
  role: 'CUSTOMER',
  fullName: 'Nguyễn Văn Test',
  phone: '0987654321',
}

Cypress.Commands.add('loginAsCustomer', (options = {}) => {
  const token = options.token || 'mock-jwt-customer'
  const user = { ...MOCK_CUSTOMER, ...options.user }

  cy.intercept('GET', '**/auth/profile', {
    statusCode: 200,
    body: user,
  }).as('getProfile')

  cy.visit(options.url || '/', {
    onBeforeLoad(win) {
      win.localStorage.setItem('token', token)
    },
  })

  if (options.waitProfile !== false) {
    cy.wait('@getProfile')
  }
})

Cypress.Commands.add('seedCustomerCart', (items, userId = 1) => {
  const cartData = {
    items,
    note: '',
  }
  cy.window().then((win) => {
    win.localStorage.setItem('token', 'mock-jwt-customer')
    win.localStorage.setItem(`cart_items_${userId}`, JSON.stringify(cartData))
  })
})
