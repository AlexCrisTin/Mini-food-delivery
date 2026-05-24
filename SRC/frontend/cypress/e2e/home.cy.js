/**
 * E2E Test: Trang chủ (HomeView)
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
    cy.get('.hero-nav .login-btn').should('be.visible')
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
    cy.get('.hero-nav .login-btn').click()
    cy.get('.login-panel.auth-dialog').should('be.visible')
  })

  it('modal có ô nhập Email', () => {
    cy.openAuthModal()
    cy.get('.auth-col--login input[type="email"]').should('be.visible')
  })

  it('modal có ô nhập Mật khẩu', () => {
    cy.openAuthModal()
    cy.get('.auth-col--login input[type="password"]').should('be.visible')
  })

  it('chuyển sang tab "Đăng ký" trong modal', () => {
    cy.openAuthModal()
    cy.openRegisterPanel()
    cy.get('.auth-col--register input[name="fullName"]').should('be.visible')
  })

  it('đóng modal bằng nút X → modal biến mất', () => {
    cy.openAuthModal()
    cy.get('.login-close').click()
    cy.get('.login-overlay').should('not.exist')
  })

  it('submit form đăng nhập rỗng → không chuyển trang', () => {
    cy.openAuthModal()
    cy.get('.auth-col--login .login-submit').click()
    cy.url().should('match', /\/$/)
  })
})

describe('Trang chủ – Điều hướng', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('click logo → vẫn ở trang chủ', () => {
    cy.get('.brand').first().click()
    cy.url().should('match', /\/$|\/home/)
  })

  it('nút "Bắt đầu ngay" mở modal đăng nhập', () => {
    cy.contains('button', 'Bắt đầu ngay').click()
    cy.get('.auth-col--login input[type="email"]').should('be.visible')
    cy.get('.auth-col--login input[type="password"]').should('be.visible')
  })
})
