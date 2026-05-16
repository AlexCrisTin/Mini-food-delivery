/**
 * E2E Test: Giỏ hàng (CartView) & Thanh toán (CheckoutView)
 * Cypress – baseUrl: http://localhost:5173
 */

const CART_ITEM = {
  lineId: '1::vua::',
  id: 1,
  name: 'Bún bò Huế',
  price: 45000,
  quantity: 2,
  restaurantId: 10,
  restaurantName: 'Quán Huế',
  size: 'Vừa',
  note: '',
  imageUrl: null,
}

const CART_ITEM_SINGLE = { ...CART_ITEM, quantity: 1 }

// ══════════════════════════════════════════════════
// CART VIEW – Giỏ hàng rỗng
// ══════════════════════════════════════════════════
describe('CartView – Giỏ hàng rỗng', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/cart' })
  })

  it('hiển thị thông báo giỏ hàng trống', () => {
    cy.contains('Giỏ hàng trống').should('be.visible')
  })

  it('có nút "Khám phá ngay" dẫn đến /browse', () => {
    cy.contains('Khám phá ngay').should('be.visible')
    cy.contains('Khám phá ngay').click()
    cy.url().should('include', '/browse')
  })

  it('tiêu đề "Giỏ hàng" hiển thị đúng', () => {
    cy.contains('Giỏ hàng').should('be.visible')
  })

  it('hiển thị "0 món"', () => {
    cy.get('[class*="item-count"]').should('contain', '0')
  })
})

// ══════════════════════════════════════════════════
// CART VIEW – Có món trong giỏ
// ══════════════════════════════════════════════════
describe('CartView – Có món trong giỏ hàng', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/cart', waitProfile: false })
    cy.seedCustomerCart([CART_ITEM])
    cy.reload()
  })

  it('hiển thị tên món đã thêm', () => {
    cy.contains('Bún bò Huế').should('be.visible')
  })

  it('hiển thị tên nhà hàng "Quán Huế"', () => {
    cy.contains('Quán Huế').should('be.visible')
  })

  it('hiển thị số lượng đúng (2)', () => {
    cy.get('[class*="qty-value"], .qty-value').should('contain', '2')
  })

  it('click nút + → số lượng tăng lên 3', () => {
    cy.get('button.qty-btn, [class*="qty-btn"]').contains('+').first().click()
    cy.get('[class*="qty-value"], .qty-value').should('contain', '3')
  })

  it('click nút − → số lượng giảm xuống 1', () => {
    cy.get('button.qty-btn, [class*="qty-btn"]').contains('−').first().click()
    cy.get('[class*="qty-value"], .qty-value').should('contain', '1')
  })

  it('click nút xóa → món bị xóa khỏi giỏ', () => {
    cy.get('button[aria-label="Xoá"], button.remove-btn, [class*="remove-btn"]')
      .first()
      .click()
    cy.contains('Giỏ hàng trống').should('be.visible')
  })

  it('hiển thị phí giao hàng 18.000 ₫', () => {
    cy.contains(/18[.,]000|18\.000/).should('be.visible')
  })

  it('hiển thị subtotal đúng: 45.000 × 2 = 90.000 ₫', () => {
    cy.contains(/90[.,]000|90\.000/).should('be.visible')
  })

  it('nút "Quay lại" dẫn về /browse', () => {
    cy.get('.back-btn, [class*="back"]').first().click()
    cy.url().should('include', '/browse')
  })
})

// ══════════════════════════════════════════════════
// CART VIEW – Giảm giá (subtotal >= 100.000 ₫)
// ══════════════════════════════════════════════════
describe('CartView – Áp dụng giảm giá', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/cart', waitProfile: false })
    cy.seedCustomerCart([
      {
        lineId: '2::lon::',
        id: 2,
        name: 'Cơm tấm sườn',
        price: 60000,
        quantity: 2,
        restaurantId: 10,
        restaurantName: 'Quán Huế',
        size: 'Lớn',
        note: '',
        imageUrl: null,
      },
    ])
    cy.reload()
  })

  it('hiển thị mức giảm giá 20.000 ₫ khi subtotal >= 100.000 ₫', () => {
    cy.contains(/20[.,]000|20\.000/).should('be.visible')
  })

  it('tổng cuối = 120.000 + 18.000 - 20.000 = 118.000 ₫', () => {
    cy.contains(/118[.,]000|118\.000/).should('be.visible')
  })
})

// ══════════════════════════════════════════════════
// CHECKOUT – Luồng đặt hàng (cần đăng nhập)
// ══════════════════════════════════════════════════
describe('CheckoutView – Đặt hàng', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/users/me/addresses**', {
      statusCode: 200,
      body: [
        {
          id: 1,
          addressLine: '123 Lê Lợi, Quận 1',
          latitude: 10.77,
          longitude: 106.7,
          isDefault: true,
        },
      ],
    }).as('getAddresses')

    cy.loginAsCustomer({ url: '/checkout', waitProfile: true })
    cy.seedCustomerCart([CART_ITEM_SINGLE])
    cy.reload()
    cy.wait('@getAddresses')
  })

  it('trang checkout hiển thị đúng tiêu đề', () => {
    cy.url().should('include', '/checkout')
    cy.contains('Thanh toán an toàn').should('be.visible')
    cy.contains('Địa chỉ giao hàng').should('be.visible')
  })

  it('hiển thị thẻ địa chỉ đã lưu', () => {
    cy.get('.address-card').should('have.length.at.least', 1)
    cy.contains('123 Lê Lợi').should('be.visible')
  })

  it('có ô ghi chú đơn hàng', () => {
    cy.get('textarea[placeholder*="Ví dụ" i]').should('be.visible')
  })

  it('hiển thị tóm tắt đơn hàng (tên món ăn)', () => {
    cy.contains('Bún bò Huế').should('be.visible')
  })

  it('đặt hàng thành công → mock API → chuyển trang đơn hàng', () => {
    cy.intercept('POST', '**/orders', {
      statusCode: 201,
      body: { id: 999, status: 'PENDING' },
    }).as('placeOrder')

    cy.contains('button', 'Xác nhận đặt đơn').click()

    cy.wait('@placeOrder')
    cy.url().should('include', '/browse')
    cy.url().should('include', 'view=orders')
  })

  it('không có địa chỉ → hiện thông báo thêm địa chỉ', () => {
    cy.intercept('GET', '**/users/me/addresses**', { statusCode: 200, body: [] }).as('noAddr')
    cy.reload()
    cy.wait('@noAddr')
    cy.contains('Bạn chưa có địa chỉ nào').should('be.visible')
    cy.contains('Thêm địa chỉ').should('be.visible')
  })
})

// ══════════════════════════════════════════════════
// CHECKOUT – Chưa đăng nhập
// ══════════════════════════════════════════════════
describe('CheckoutView – Chưa đăng nhập', () => {
  it('truy cập /checkout khi chưa đăng nhập → redirect về / hoặc /login', () => {
    cy.visit('/checkout')
    cy.url().should('match', /\/$|\/login|\/home/)
  })
})
