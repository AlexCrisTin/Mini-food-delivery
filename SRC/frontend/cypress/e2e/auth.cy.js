/**
 * E2E Test: Luồng Đăng nhập / Đăng ký / Đăng xuất
 */

const VALID_EMAIL = 'testuser@example.com'
const VALID_PASSWORD = 'Test@123456'
const WRONG_EMAIL = 'wrong@example.com'
const WRONG_PASSWORD = 'WrongPass999'

const LOGIN_SUCCESS_BODY = {
  accessToken: 'mock-jwt-token-xyz',
  refreshToken: 'mock-refresh-token',
  userId: 1,
  email: VALID_EMAIL,
  fullName: 'Test User',
  role: 'CUSTOMER',
}

function openLoginModal() {
  cy.openAuthModal()
}

function getLoginForm() {
  return cy.get('.auth-col--login form.login-form')
}

function getRegisterForm() {
  return cy.get('.auth-col--register form.login-form')
}

describe('Đăng nhập – Validation', () => {
  beforeEach(openLoginModal)

  it('để trống email và mật khẩu → không gọi API', () => {
    cy.intercept('POST', '**/api/auth/login').as('loginCall')
    getLoginForm().find('button.login-submit').click()
    cy.get('@loginCall').should('not.exist')
  })

  it('nhập email sai định dạng → HTML5 validation hoặc thông báo lỗi', () => {
    getLoginForm().find('input[type="email"]').type('not-an-email')
    getLoginForm().find('input[type="password"]').type('anypassword')
    getLoginForm().find('button.login-submit').click()
    getLoginForm()
      .find('input[type="email"]')
      .then(($el) => {
        const isValid = $el[0].checkValidity()
        if (!isValid) {
          expect(isValid).to.be.false
        } else {
          cy.get('.login-error').should('be.visible')
        }
      })
  })

  it('nhập mật khẩu < 6 ký tự → có thông báo lỗi hoặc chặn submit', () => {
    getLoginForm().find('input[type="email"]').type(VALID_EMAIL)
    getLoginForm().find('input[type="password"]').type('123')
    getLoginForm().find('button.login-submit').click()
    cy.url().should('not.include', '/browse')
  })
})

describe('Đăng nhập – Mock API response', () => {
  beforeEach(openLoginModal)

  it('API trả về 200 → chuyển hướng sau đăng nhập', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: LOGIN_SUCCESS_BODY,
    }).as('loginSuccess')

    getLoginForm().find('input[type="email"]').type(VALID_EMAIL)
    getLoginForm().find('input[type="password"]').type(VALID_PASSWORD)
    getLoginForm().find('button.login-submit').click()

    cy.wait('@loginSuccess')
    cy.url().should('include', '/browse')
  })

  it('API trả về 401 → hiển thị thông báo lỗi', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: { message: 'Email hoặc mật khẩu không đúng' },
    }).as('loginFail')

    getLoginForm().find('input[type="email"]').type(WRONG_EMAIL)
    getLoginForm().find('input[type="password"]').type(WRONG_PASSWORD)
    getLoginForm().find('button.login-submit').click()

    cy.wait('@loginFail')
    cy.get('.login-error').should('be.visible')
  })

  it('token được lưu vào localStorage sau đăng nhập thành công', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: LOGIN_SUCCESS_BODY,
    }).as('loginOk')

    getLoginForm().find('input[type="email"]').type(VALID_EMAIL)
    getLoginForm().find('input[type="password"]').type(VALID_PASSWORD)
    getLoginForm().find('button.login-submit').click()

    cy.wait('@loginOk')
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.equal('mock-jwt-token-xyz')
    })
  })
})

describe('Đăng ký – Validation', () => {
  beforeEach(() => {
    openLoginModal()
    cy.openRegisterPanel()
  })

  it('hiển thị form đăng ký sau khi chuyển tab', () => {
    getRegisterForm().find('input[name="regEmail"]').should('be.visible')
    getRegisterForm().find('input[name="newPassword"]').should('be.visible')
  })

  it('mật khẩu xác nhận không khớp → hiện lỗi', () => {
    getRegisterForm().find('input[name="fullName"]').type('Nguyễn Văn Test')
    getRegisterForm().find('input[name="regEmail"]').type('newuser@test.com')
    getRegisterForm().find('input[name="newPassword"]').type('Password123')
    getRegisterForm().find('input[name="confirmPassword"]').type('DifferentPass456')
    getRegisterForm().find('button.login-submit').click()
    cy.get('.login-error').should('contain', 'Mat khau xac nhan khong khop')
  })

  it('email đã tồn tại → API 409 → hiện thông báo lỗi', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 409,
      body: { message: 'Email đã được sử dụng' },
    }).as('registerDup')

    getRegisterForm().find('input[name="fullName"]').type('Dup User')
    getRegisterForm().find('input[name="regEmail"]').type('existing@test.com')
    getRegisterForm().find('input[name="newPassword"]').type('Password123')
    getRegisterForm().find('input[name="confirmPassword"]').type('Password123')
    getRegisterForm().find('button.login-submit').click()

    cy.wait('@registerDup')
    cy.get('.login-error').should('be.visible')
  })

  it('đăng ký thành công → mock 201 → chuyển trang', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 201,
      body: {
        accessToken: 'new-user-token',
        refreshToken: 'new-refresh-token',
        userId: 99,
        email: 'brand@new.com',
        fullName: 'Brand New',
        role: 'CUSTOMER',
      },
    }).as('registerOk')

    getRegisterForm().find('input[name="fullName"]').type('Brand New')
    getRegisterForm().find('input[name="regEmail"]').type('brand@new.com')
    getRegisterForm().find('input[name="newPassword"]').type('Secure@Pass1')
    getRegisterForm().find('input[name="confirmPassword"]').type('Secure@Pass1')
    getRegisterForm().find('button.login-submit').click()

    cy.wait('@registerOk')
    cy.url().should('include', '/browse')
  })
})

describe('Đăng xuất', () => {
  it('sau đăng xuất → token bị xóa khỏi localStorage', () => {
    cy.loginAsCustomer({ url: '/profile' })
    cy.contains('button', 'Đăng xuất').click()
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.be.null
    })
  })
})
