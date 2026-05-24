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

const MOCK_RESTAURANTS = [
  {
    id: 1,
    name: 'Quán Huế',
    rating: 4.5,
    latitude: 10.776,
    longitude: 106.7,
    address: '123 Nguyễn Huệ, Huế',
    imageUrl: null,
  },
  {
    id: 2,
    name: 'Phở Hà Nội',
    rating: 4.2,
    latitude: 21.028,
    longitude: 105.854,
    address: '45 Lê Lợi, Hà Nội',
    imageUrl: null,
  },
]

const MOCK_MENU_BY_RESTAURANT = {
  1: [
    {
      id: 101,
      name: 'Bún bò Huế',
      price: 45000,
      categoryName: 'Món chính',
      isAvailable: true,
      restaurantId: 1,
    },
    {
      id: 102,
      name: 'Chả lụa',
      price: 15000,
      categoryName: 'Thêm',
      isAvailable: true,
      restaurantId: 1,
    },
  ],
  2: [
    {
      id: 201,
      name: 'Phở bò',
      price: 50000,
      categoryName: 'Món chính',
      isAvailable: true,
      restaurantId: 2,
    },
  ],
  10: [
    {
      id: 1,
      name: 'Bún bò Huế',
      price: 45000,
      categoryName: 'Món chính',
      isAvailable: true,
      restaurantId: 10,
    },
  ],
}

function buildRestaurantDetail(restaurantId) {
  const base =
    MOCK_RESTAURANTS.find((item) => item.id === Number(restaurantId)) ||
    MOCK_MENU_BY_RESTAURANT[restaurantId]
      ? {
          id: Number(restaurantId),
          name: 'Quán Huế',
          rating: 4.5,
          latitude: 10.776,
          longitude: 106.7,
          address: '123 Nguyễn Huệ, Huế',
        }
      : null

  if (!base) {
    return { id: Number(restaurantId), name: 'Nhà hàng test', menuItems: [] }
  }

  return {
    ...base,
    menuItems: MOCK_MENU_BY_RESTAURANT[restaurantId] || MOCK_MENU_BY_RESTAURANT[1] || [],
  }
}

Cypress.Commands.add('mockCustomerSession', (options = {}) => {
  const user = { ...MOCK_CUSTOMER, ...options.user }

  cy.intercept('GET', '**/api/users/me', {
    statusCode: 200,
    body: user,
  }).as('getProfile')

  cy.intercept('GET', '**/api/users/me/addresses**', {
    statusCode: 200,
    body: options.addresses || [],
  }).as('getAddresses')

  cy.intercept('GET', '**/api/users/me/notifications**', {
    statusCode: 200,
    body: [],
  }).as('getNotifications')
})

Cypress.Commands.add('mockBrowseData', (options = {}) => {
  const restaurants = options.restaurants || MOCK_RESTAURANTS

  cy.intercept('POST', '**/api/restaurants/search', {
    statusCode: 200,
    body: restaurants,
  }).as('searchRestaurants')

  cy.intercept('GET', '**/api/restaurant-categories', {
    statusCode: 200,
    body: [{ id: 1, name: 'Bún bò' }],
  }).as('getCategories')

  cy.intercept('GET', '**/api/orders/history', {
    statusCode: 200,
    body: options.orders || [],
  }).as('getOrderHistory')

  cy.intercept('GET', '**/api/restaurants/*', (req) => {
    const match = req.url.match(/\/api\/restaurants\/(\d+)(?:\?.*)?$/)
    if (!match) return
    const restaurantId = Number(match[1])
    req.reply({
      statusCode: 200,
      body: buildRestaurantDetail(restaurantId),
    })
  }).as('getRestaurantById')
})

Cypress.Commands.add('mockRestaurantDetail', (restaurantId = 1) => {
  cy.intercept('GET', `**/api/restaurants/${restaurantId}`, {
    statusCode: 200,
    body: buildRestaurantDetail(restaurantId),
  }).as('getRestaurantDetail')
})

Cypress.Commands.add('mockCartPricingApis', (restaurantId = 10) => {
  cy.intercept('GET', `**/api/restaurants/${restaurantId}`, {
    statusCode: 200,
    body: buildRestaurantDetail(restaurantId),
  }).as('getCartRestaurant')
})

Cypress.Commands.add('loginAsCustomer', (options = {}) => {
  const token = options.token || 'mock-jwt-customer'
  const user = { ...MOCK_CUSTOMER, ...options.user }

  cy.mockCustomerSession(options)
  cy.mockBrowseData(options)

  if (options.restaurantId || options.url?.includes('/cart') || options.url?.includes('/checkout')) {
    cy.mockCartPricingApis(options.restaurantId || 10)
  }

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

Cypress.Commands.add('openAuthModal', () => {
  cy.visit('/')
  cy.get('.hero-nav .login-btn').click()
  cy.get('.login-panel.auth-dialog').should('be.visible')
})

Cypress.Commands.add('openRegisterPanel', () => {
  cy.get('.auth-col--login .auth-link').contains('Đăng ký').click()
  cy.get('.auth-inner').should('have.class', 'auth-inner--register')
})
