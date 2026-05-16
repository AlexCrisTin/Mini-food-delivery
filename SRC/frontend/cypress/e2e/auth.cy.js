/**
 * E2E Test: Luồng Đăng nhập / Đăng ký / Đăng xuất
 * Cypress – baseUrl: http://localhost:5173
 *
 * ⚠️  Thay đổi VALID_EMAIL / VALID_PASSWORD thành tài khoản test thực tế
 *     hoặc dùng cy.intercept() để mock API nếu backend chưa chạy.
 */

const VALID_EMAIL    = 'testuser@example.com'
const VALID_PASSWORD = 'Test@123456'
const WRONG_EMAIL    = 'wrong@example.com'
const WRONG_PASSWORD = 'WrongPass999'

// ──────────────────────────────────────────────────────────────
// Helper: mở modal đăng nhập từ trang chủ
// ──────────────────────────────────────────────────────────────
function openLoginModal() {
  cy.visit('/')
  cy.contains('Đăng nhập').first().click()
  cy.get('input[type="email"]').should('be.visible')
}

// ──────────────────────────────────────────────────────────────
// ĐĂNG NHẬP – Validation phía client
// ──────────────────────────────────────────────────────────────
describe('Đăng nhập – Validation', () => {
  beforeEach(openLoginModal)

  it('để trống email và mật khẩu → không gọi API', () => {
    cy.intercept('POST', '**/auth/login').as('loginCall')
    cy.get('form button[type="submit"], button[class*="submit"], button[class*="login"]')
      .first()
      .click()
    cy.get('@loginCall').should('not.exist')
  })

  it('nhập email sai định dạng → HTML5 validation hoặc thông báo lỗi', () => {
    cy.get('input[type="email"]').type('not-an-email')
    cy.get('input[type="password"]').type('anypassword')
    cy.get('form button[type="submit"], button[class*="login"]').first().click()
    // Kiểm tra HTML5 validity hoặc error message
    cy.get('input[type="email"]').then(($el) => {
      const isValid = ($el[0]).checkValidity()
      if (!isValid) {
        expect(isValid).to.be.false
      } else {
        cy.get('[class*="error"], .error-msg, [class*="alert"]').should('be.visible')
      }
    })
  })

  it('nhập mật khẩu < 6 ký tự → có thông báo lỗi hoặc chặn submit', () => {
    cy.get('input[type="email"]').type(VALID_EMAIL)
    cy.get('input[type="password"]').type('123')
    cy.get('form button[type="submit"], button[class*="login"]').first().click()
    cy.url().should('not.include', '/browse')
  })
})

// ──────────────────────────────────────────────────────────────
// ĐĂNG NHẬP – Mock API (không phụ thuộc backend)
// ──────────────────────────────────────────────────────────────
describe('Đăng nhập – Mock API response', () => {
  beforeEach(openLoginModal)

  it('API trả về 200 → chuyển hướng sau đăng nhập', () => {
    cy.intercept('POST', '**/auth/login', {
      statusCode: 200,
      body: {
        token: 'mock-jwt-token-xyz',
        user: { id: 1, email: VALID_EMAIL, role: 'CUSTOMER', fullName: 'Test User' },
      },
    }).as('loginSuccess')

    cy.get('input[type="email"]').type(VALID_EMAIL)
    cy.get('input[type="password"]').type(VALID_PASSWORD)
    cy.get('form button[type="submit"], button[class*="login"]').first().click()

    cy.wait('@loginSuccess')
    cy.url().should('not.include', '/login')
  })

  it('API trả về 401 → hiển thị thông báo lỗi', () => {
    cy.intercept('POST', '**/auth/login', {
      statusCode: 401,
      body: { message: 'Email hoặc mật khẩu không đúng' },
    }).as('loginFail')

    cy.get('input[type="email"]').type(WRONG_EMAIL)
    cy.get('input[type="password"]').type(WRONG_PASSWORD)
    cy.get('form button[type="submit"], button[class*="login"]').first().click()

    cy.wait('@loginFail')
    cy.get('[class*="error"], .error-msg, [class*="alert"]').should('be.visible')
  })

  it('token được lưu vào localStorage sau đăng nhập thành công', () => {
    cy.intercept('POST', '**/auth/login', {
      statusCode: 200,
      body: {
        token: 'mock-jwt-token-xyz',
        user: { id: 1, email: VALID_EMAIL, role: 'CUSTOMER' },
      },
    }).as('loginOk')

    cy.get('input[type="email"]').type(VALID_EMAIL)
    cy.get('input[type="password"]').type(VALID_PASSWORD)
    cy.get('form button[type="submit"], button[class*="login"]').first().click()

    cy.wait('@loginOk')
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.equal('mock-jwt-token-xyz')
    })
  })
})

// ──────────────────────────────────────────────────────────────
// ĐĂNG KÝ – Validation
// ──────────────────────────────────────────────────────────────
describe('Đăng ký – Validation', () => {
  beforeEach(() => {
    openLoginModal()
    cy.contains('Đăng ký').click()
  })

  it('hiển thị form đăng ký sau khi chuyển tab', () => {
    cy.get('input[type="email"]').should('be.visible')
    cy.get('input[type="password"]').should('be.visible')
  })

  it('mật khẩu xác nhận không khớp → hiện lỗi', () => {
    cy.get('input[placeholder*="tên" i], input[placeholder*="fullname" i]')
      .first()
      .type('Nguyễn Văn Test')
    cy.get('input[type="email"]').type('newuser@test.com')

    const pwdFields = cy.get('input[type="password"]')
    pwdFields.first().type('Password123')
    pwdFields.last().type('DifferentPass456')

    cy.get('form button[type="submit"], button[class*="register"], button[class*="submit"]')
      .first()
      .click()
    cy.get('[class*="error"], .error-msg, [class*="alert"]').should('be.visible')
  })

  it('email đã tồn tại → API 409 → hiện thông báo lỗi', () => {
    cy.intercept('POST', '**/auth/register', {
      statusCode: 409,
      body: { message: 'Email đã được sử dụng' },
    }).as('registerDup')

    cy.get('input[placeholder*="tên" i], input[placeholder*="fullname" i]')
      .first()
      .type('Dup User')
    cy.get('input[type="email"]').type('existing@test.com')

    const pwdFields = cy.get('input[type="password"]')
    pwdFields.first().type('Password123')
    pwdFields.last().type('Password123')

    cy.get('form button[type="submit"], button[class*="register"], button[class*="submit"]')
      .first()
      .click()

    cy.wait('@registerDup')
    cy.get('[class*="error"], .error-msg, [class*="alert"]').should('be.visible')
  })

  it('đăng ký thành công → mock 201 → chuyển trang', () => {
    cy.intercept('POST', '**/auth/register', {
      statusCode: 201,
      body: {
        token: 'new-user-token',
        user: { id: 99, email: 'brand@new.com', role: 'CUSTOMER', fullName: 'Brand New' },
      },
    }).as('registerOk')

    cy.get('input[placeholder*="tên" i], input[placeholder*="fullname" i]')
      .first()
      .type('Brand New')
    cy.get('input[type="email"]').type('brand@new.com')

    const pwdFields = cy.get('input[type="password"]')
    pwdFields.first().type('Secure@Pass1')
    pwdFields.last().type('Secure@Pass1')

    cy.get('form button[type="submit"], button[class*="register"], button[class*="submit"]')
      .first()
      .click()

    cy.wait('@registerOk')
    cy.url().should('not.include', '/register')
  })
})

// ──────────────────────────────────────────────────────────────
// ĐĂNG XUẤT
// ──────────────────────────────────────────────────────────────
describe('Đăng xuất', () => {
  beforeEach(() => {
    // Giả lập đã đăng nhập bằng cách set localStorage trực tiếp
    cy.visit('/', {
      onBeforeLoad(win) {
        win.localStorage.setItem('token', 'mock-jwt-token-xyz')
      },
    })
    cy.intercept('GET', '**/auth/profile', {
      statusCode: 200,
      body: { id: 1, email: VALID_EMAIL, role: 'CUSTOMER', fullName: 'Test User' },
    }).as('getProfile')
  })

  it('sau đăng xuất → token bị xóa khỏi localStorage', () => {
    cy.intercept('POST', '**/auth/logout', { statusCode: 200, body: {} }).as('logoutCall')
    // Tìm nút đăng xuất (thường xuất hiện sau khi đăng nhập)
    cy.get('[class*="logout"], button[class*="logout"]').first().click({ force: true })
    cy.wait('@logoutCall')
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.be.null
    })
  })
})
