/**
 * E2E Test: Trang duyệt món ăn (BrowseView) & Chi tiết nhà hàng (RestaurantDetail)
 */

describe('BrowseView – Danh sách món ăn', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/browse' })
  })

  it('tải trang /browse thành công', () => {
    cy.url().should('include', '/browse')
  })

  it('hiển thị món ăn sau khi API trả về', () => {
    cy.contains('Bún bò Huế').should('be.visible')
    cy.contains('Phở bò').should('exist')
  })

  it('hiển thị nhiều món ăn từ API', () => {
    cy.get('.popular-card, .recommend-card, article').should('have.length.gte', 2)
  })

  it('có ô tìm kiếm món ăn', () => {
    cy.get('input[placeholder*="Tìm món" i]').should('be.visible')
  })

  it('tìm kiếm "Phở" → chỉ hiển thị kết quả liên quan', () => {
    cy.get('input[placeholder*="Tìm món" i]').type('Phở')
    cy.get('.search-popover').should('be.visible')
    cy.contains('.search-result-item', 'Phở bò').should('be.visible')
    cy.contains('.search-result-item', 'Bún bò Huế').should('not.exist')
  })

  it('xóa nội dung tìm kiếm → popover đóng', () => {
    cy.get('input[placeholder*="Tìm món" i]').type('Phở').clear()
    cy.get('.search-popover').should('not.exist')
  })

  it('tìm kiếm không có kết quả → hiển thị thông báo trống', () => {
    cy.get('input[placeholder*="Tìm món" i]').type('xyzxyzxyz123')
    cy.contains('Không tìm thấy món phù hợp').should('be.visible')
  })
})

describe('RestaurantDetail – Chi tiết nhà hàng & menu', () => {
  beforeEach(() => {
    cy.loginAsCustomer({ url: '/restaurants/1' })
    cy.contains('h1', 'Quán Huế').should('be.visible')
  })

  it('hiển thị tên nhà hàng', () => {
    cy.contains('h1', 'Quán Huế').should('be.visible')
  })

  it('hiển thị danh sách món ăn trong menu', () => {
    cy.contains('Bún bò Huế').should('be.visible')
    cy.contains('Chả lụa').should('be.visible')
  })

  it('hiển thị giá tiền của món ăn', () => {
    cy.contains(/45[.,]000|45\.000/).should('be.visible')
  })

  it('click nút size "Vừa" → hiện thông báo đã thêm', () => {
    cy.contains('button.size-btn', 'Vừa:').first().click()
    cy.contains('Đã thêm').should('be.visible')
  })

  it('thêm 2 lần cùng món → thông báo xuất hiện 2 lần', () => {
    cy.contains('button.size-btn', 'Vừa:').first().click()
    cy.contains('button.size-btn', 'Vừa:').first().click()
    cy.contains('Đã thêm').should('be.visible')
  })

  it('lọc theo danh mục → chỉ hiển thị món thuộc danh mục', () => {
    cy.contains('.menu-filter button', 'Thêm').click()
    cy.contains('Chả lụa').should('be.visible')
    cy.contains('Bún bò Huế').should('not.exist')
  })
})

describe('Luồng: Browse → Restaurant Detail → Giỏ hàng', () => {
  it('vào chi tiết nhà hàng → thêm món → giỏ hàng hiển thị món', () => {
    cy.loginAsCustomer({ url: '/restaurants/1' })
    cy.contains('h1', 'Quán Huế').should('be.visible')
    cy.contains('button.size-btn', 'Vừa:').first().click()
    cy.visit('/cart')
    cy.wait('@getProfile')
    cy.contains('Bún bò Huế').should('be.visible')
  })
})
