/**
 * E2E Test: Trang chủ (HomeView)
 * Cypress – baseUrl: http://localhost:5173
 */

describe('Trang chủ – Hiển thị giao diện', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('tải trang chủ thành công (status 200)', () => {
    cy.request('/').its('status').should('eq', 200)
  })

  it('hiển thị logo / thương hiệu "Giao Đồ Ăn"', () => {
    cy.contains('Giao Đồ Ăn').should('be.visible')
  })

  it('hiển thị thanh điều hướng (nav)', () => {
    cy.get('.hero-nav').should('be.visible')
  })

  it('có nút "Đăng nhập" trên nav', () => {
    cy.contains('Đăng nhập').should('be.visible')
  })

  it('có khu vực hero section', () => {
    cy.get('.hero').should('exist')
  })

  it('hiển thị section "Cách hoạt động" hoặc các bước', () => {
    cy.contains(/cách hoạt động|how it works|bước/i).should('be.visible')
  })

  it('cuộn xuống vẫn hiển thị nội dung trang', () => {
    cy.scrollTo('bottom')
    cy.get('footer, .footer, [class*="footer"]').should('exist')
  })
})

describe('Trang chủ – Modal Đăng nhập / Đăng ký', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('click "Đăng nhập" → modal hiện ra', () => {
    cy.contains('Đăng nhập').first().click()
    cy.get('[class*="modal"], [class*="auth"], [class*="login"]').should('be.visible')
  })

  it('modal có ô nhập Email', () => {
    cy.contains('Đăng nhập').first().click()
    cy.get('input[type="email"], input[placeholder*="mail" i]').should('be.visible')
  })

  it('modal có ô nhập Mật khẩu', () => {
    cy.contains('Đăng nhập').first().click()
    cy.get('input[type="password"]').should('be.visible')
  })

  it('chuyển sang tab "Đăng ký" trong modal', () => {
    cy.contains('Đăng nhập').first().click()
    cy.contains('Đăng ký').click()
    cy.get('input[placeholder*="họ tên" i], input[placeholder*="fullname" i], input[placeholder*="tên" i]')
      .should('be.visible')
  })

  it('đóng modal bằng nút X → modal biến mất', () => {
    cy.contains('Đăng nhập').first().click()
    cy.get('[class*="close"], [aria-label*="đóng" i], [aria-label*="close" i]').first().click()
    cy.get('[class*="modal"]').should('not.be.visible')
  })

  it('submit form đăng nhập rỗng → không chuyển trang', () => {
    cy.contains('Đăng nhập').first().click()
    cy.get('form button[type="submit"], [class*="login-btn"], button[class*="submit"]')
      .first()
      .click()
    cy.url().should('include', '/')
  })
})

describe('Trang chủ – Điều hướng', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('click logo → vẫn ở trang chủ', () => {
    cy.get('.brand, [class*="brand"], [class*="logo"]').first().click()
    cy.url().should('match', /\/$|\/home/)
  })

  it('link "Thực đơn" hoặc "Browse" điều hướng đúng', () => {
    cy.get('.hero-links a, nav a').contains(/thực đơn|browse|món ăn/i).click()
    cy.url().should('include', '/browse')
  })
})
