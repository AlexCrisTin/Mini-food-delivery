/**
 * E2E: Lịch sử đơn hàng & Theo dõi đơn (OrderHistory, OrderTracking)
 */

const mockOrders = [
  {
    id: 501,
    status: 'DELIVERED',
    totalAmount: 108000,
    restaurantName: 'Quán Huế',
    createdAt: '2025-06-01T10:00:00',
    items: [{ menuItemName: 'Bún bò Huế', quantity: 1, unitPrice: 90000 }],
  },
  {
    id: 502,
    status: 'SHIPPING',
    totalAmount: 68000,
    restaurantName: 'Phở Hà Nội',
    createdAt: '2025-06-02T14:30:00',
    items: [{ menuItemName: 'Phở bò', quantity: 1, unitPrice: 50000 }],
  },
]

const mockOrderDetail = {
  id: 502,
  status: 'SHIPPING',
  totalAmount: 68000,
  restaurantName: 'Phở Hà Nội',
  restaurantId: 2,
  createdAt: '2025-06-02T14:30:00',
  deliveryAddress: '123 Lê Lợi, Q1',
  items: [{ menuItemName: 'Phở bò', quantity: 1, unitPrice: 50000 }],
}

describe('Lịch sử đơn hàng', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/orders/history', orders: mockOrders })
  })

  it('hiển thị tiêu đề trang', () => {
    cy.contains('h1', 'Lịch sử đơn hàng').should('be.visible')
  })

  it('hiển thị danh sách đơn từ API', () => {
    cy.contains('Quán Huế').should('be.visible')
    cy.contains('Phở Hà Nội').should('be.visible')
  })

  it('hiển thị tổng tiền định dạng VND', () => {
    cy.contains(/108[.,]000|108\.000/).should('be.visible')
  })
})

describe('Theo dõi đơn hàng', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/orders/502', {
      statusCode: 200,
      body: mockOrderDetail,
    }).as('getOrder')

    cy.loginAsCustomer({ url: '/orders/502/tracking' })
    cy.wait('@getOrder')
  })

  it('hiển thị mã đơn #502', () => {
    cy.contains('Đơn hàng #502').should('be.visible')
  })

  it('hiển thị bước trạng thái "Đang giao"', () => {
    cy.contains('Đang giao').should('be.visible')
  })

  it('nút quay lại lịch sử', () => {
    cy.contains('Quay lại lịch sử').should('be.visible')
  })
})

describe('Theo dõi đơn – Đơn đã hủy', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/orders/503', {
      statusCode: 200,
      body: { ...mockOrderDetail, id: 503, status: 'CANCELLED' },
    }).as('getCancelled')

    cy.loginAsCustomer({ url: '/orders/503/tracking' })
    cy.wait('@getCancelled')
  })

  it('hiển thị trạng thái đã hủy', () => {
    cy.contains(/Đã hủy|hủy/i).should('be.visible')
  })
})
