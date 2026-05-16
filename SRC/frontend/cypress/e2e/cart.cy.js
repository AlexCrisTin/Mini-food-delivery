/**
 * E2E Test: Giỏ hàng (CartView) & Thanh toán (CheckoutView)
 * Cypress – baseUrl: http://localhost:5173
 */

// ── Helpers & mock data ──────────────────────────
function seedCart(win) {
  const cartData = {
    items: [
      {
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
      },
    ],
    note: '',
  }
  win.localStorage.setItem('cart_items_guest', JSON.stringify(cartData))
}

function seedCartWithToken(win) {
  const cartData = {
    items: [
      {
        lineId: '1::vua::',
        id: 1,
        name: 'Bún bò Huế',
        price: 45000,
        quantity: 1,
        restaurantId: 10,
        restaurantName: 'Quán Huế',
        size: 'Vừa',
        note: '',
        imageUrl: null,
      },
    ],
    note: '',
  }
  win.localStorage.setItem('token', 'mock-jwt-token')
  win.localStorage.setItem('cart_items_1', JSON.stringify(cartData))
}

// ══════════════════════════════════════════════════
// CART VIEW – Giỏ hàng rỗng
// ══════════════════════════════════════════════════
describe('CartView – Giỏ hàng rỗng', () => {
  beforeEach(() => {
    cy.visit('/cart')
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
    cy.visit('/cart', { onBeforeLoad: seedCart })
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
    cy.visit('/cart', {
      onBeforeLoad(win) {
        const cartData = {
          items: [
            {
              lineId: '2::lon::',
              id: 2,
              name: 'Cơm tấm sườn',
              price: 60000,
              quantity: 2,  // 120.000 ₫ >= ngưỡng 100.000 ₫
              restaurantId: 10,
              restaurantName: 'Quán Huế',
              size: 'Lớn',
              note: '',
              imageUrl: null,
            },
          ],
          note: '',
        }
        win.localStorage.setItem('cart_items_guest', JSON.stringify(cartData))
      },
    })
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
    cy.intercept('GET', '**/auth/profile', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'customer@test.com',
        role: 'CUSTOMER',
        fullName: 'Nguyễn Văn A',
        phone: '0987654321',
        address: '123 Lê Lợi, Quận 1',
      },
    }).as('getProfile')

    cy.visit('/checkout', { onBeforeLoad: seedCartWithToken })
  })

  it('trang checkout hiển thị thông tin giao hàng', () => {
    cy.url().should('include', '/checkout')
    cy.contains('Thông tin giao hàng').should('be.visible')
  })

  it('có ô nhập địa chỉ giao hàng', () => {
    cy.get('input[placeholder*="địa chỉ" i], textarea[placeholder*="địa chỉ" i]')
      .should('be.visible')
  })

  it('có ô ghi chú đơn hàng', () => {
    cy.get(
      'textarea[placeholder*="ghi chú" i], input[placeholder*="ghi chú" i], textarea[placeholder*="note" i]',
    ).should('be.visible')
  })

  it('hiển thị tóm tắt đơn hàng (tên món ăn)', () => {
    cy.contains('Bún bò Huế').should('be.visible')
  })

  it('đặt hàng thành công → mock API → chuyển trang xác nhận', () => {
    cy.intercept('POST', '**/orders', {
      statusCode: 201,
      body: { orderId: 999, status: 'PENDING', message: 'Đặt hàng thành công' },
    }).as('placeOrder')

    cy.get('input[placeholder*="địa chỉ" i]')
      .first()
      .clear()
      .type('456 Nguyễn Huệ, Quận 1, TP.HCM')

    cy.get('button[class*="submit"], button[class*="order"], button[class*="checkout"]')
      .contains(/đặt hàng|xác nhận|order/i)
      .click()

    cy.wait('@placeOrder')
    cy.url().should('match', /\/order|\/tracking|\/success|\/confirm/)
  })

  it('địa chỉ trống → không cho đặt hàng', () => {
    cy.intercept('POST', '**/orders').as('orderAttempt')

    cy.get('input[placeholder*="địa chỉ" i]').first().clear()
    cy.get('button').contains(/đặt hàng|xác nhận|order/i).click()

    // Không call API hoặc hiện lỗi
    cy.get('[class*="error"], .error, [class*="alert"]')
      .should('be.visible')
      .or(cy.get('@orderAttempt').should('not.exist'))
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
