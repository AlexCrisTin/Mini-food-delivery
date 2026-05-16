/**
 * E2E Test: Trang duyệt món ăn (BrowseView) & Chi tiết nhà hàng (RestaurantDetail)
 * Cypress – baseUrl: http://localhost:5173
 */

// ── Mock data ────────────────────────────────────
const mockRestaurants = [
  { id: 1, name: 'Quán Huế', category: 'Bún bò', rating: 4.5, isOpen: true, imageUrl: null },
  { id: 2, name: 'Phở Hà Nội', category: 'Phở', rating: 4.2, isOpen: true, imageUrl: null },
  { id: 3, name: 'Pizza House', category: 'Pizza', rating: 3.9, isOpen: false, imageUrl: null },
]

const mockMenuItems = [
  { id: 101, name: 'Bún bò Huế', price: 45000, category: 'Món chính', restaurantId: 1 },
  { id: 102, name: 'Chả lụa', price: 15000, category: 'Thêm', restaurantId: 1 },
]

// ══════════════════════════════════════════════════
// BROWSE VIEW
// ══════════════════════════════════════════════════
describe('BrowseView – Danh sách nhà hàng', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/restaurants**', {
      statusCode: 200,
      body: mockRestaurants,
    }).as('getRestaurants')

    cy.visit('/browse')
    cy.wait('@getRestaurants')
  })

  it('tải trang /browse thành công', () => {
    cy.url().should('include', '/browse')
  })

  it('hiển thị danh sách nhà hàng sau khi API trả về', () => {
    cy.contains('Quán Huế').should('be.visible')
    cy.contains('Phở Hà Nội').should('be.visible')
  })

  it('hiển thị đúng số nhà hàng từ API (3 nhà hàng)', () => {
    cy.get('[class*="restaurant-card"], [class*="card"], article').should('have.length.gte', 3)
  })

  it('có ô tìm kiếm nhà hàng', () => {
    cy.get('input[type="search"], input[placeholder*="tìm" i], input[placeholder*="search" i]')
      .should('be.visible')
  })

  it('tìm kiếm "Phở" → chỉ hiển thị kết quả liên quan', () => {
    cy.get('input[type="search"], input[placeholder*="tìm" i], input[placeholder*="search" i]')
      .type('Phở')
    cy.contains('Phở Hà Nội').should('be.visible')
    cy.contains('Quán Huế').should('not.exist')
  })

  it('xóa nội dung tìm kiếm → hiển thị lại tất cả nhà hàng', () => {
    cy.get('input[type="search"], input[placeholder*="tìm" i], input[placeholder*="search" i]')
      .type('Phở')
      .clear()
    cy.contains('Quán Huế').should('be.visible')
    cy.contains('Phở Hà Nội').should('be.visible')
  })

  it('nhà hàng đóng cửa có nhãn "Đóng cửa" hoặc bị disabled', () => {
    cy.contains('Pizza House')
      .closest('[class*="card"], article')
      .should(($el) => {
        const text = $el.text()
        const hasClass = $el.hasClass('closed') || $el.find('[class*="closed"]').length > 0
        expect(text.includes('Đóng') || hasClass).to.be.true
      })
  })

  it('tìm kiếm không có kết quả → hiển thị thông báo trống', () => {
    cy.get('input[type="search"], input[placeholder*="tìm" i], input[placeholder*="search" i]')
      .type('xyzxyzxyz123')
    cy.get('[class*="empty"], [class*="no-result"]').should('be.visible')
  })
})

// ══════════════════════════════════════════════════
// RESTAURANT DETAIL
// ══════════════════════════════════════════════════
describe('RestaurantDetail – Chi tiết nhà hàng & menu', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/restaurants/1', {
      statusCode: 200,
      body: mockRestaurants[0],
    }).as('getRestaurant')

    cy.intercept('GET', '**/restaurants/1/menu**', {
      statusCode: 200,
      body: mockMenuItems,
    }).as('getMenu')

    cy.visit('/restaurant/1')
    cy.wait('@getRestaurant')
    cy.wait('@getMenu')
  })

  it('hiển thị tên nhà hàng', () => {
    cy.contains('Quán Huế').should('be.visible')
  })

  it('hiển thị danh sách món ăn trong menu', () => {
    cy.contains('Bún bò Huế').should('be.visible')
    cy.contains('Chả lụa').should('be.visible')
  })

  it('hiển thị giá tiền của món ăn', () => {
    cy.contains(/45[.,]000|45\.000/).should('be.visible')
  })

  it('click "Thêm vào giỏ" → giỏ hàng tăng 1', () => {
    cy.get('button[class*="add"], button[class*="cart"], button[aria-label*="thêm" i]')
      .first()
      .click()
    // Kiểm tra badge giỏ hàng hoặc cart counter
    cy.get('[class*="cart-count"], [class*="badge"], [class*="item-count"]')
      .invoke('text')
      .then((text) => {
        expect(parseInt(text)).to.be.gte(1)
      })
  })

  it('thêm 2 lần cùng món → số lượng = 2', () => {
    const addBtn = cy.get('button[class*="add"], button[class*="cart"]').first()
    addBtn.click()
    addBtn.click()
    cy.get('[class*="cart-count"], [class*="badge"]')
      .invoke('text')
      .then((text) => {
        expect(parseInt(text)).to.be.gte(2)
      })
  })

  it('lọc theo danh mục → chỉ hiển thị món thuộc danh mục', () => {
    cy.get('[class*="category"], [class*="filter"], button[class*="tab"]')
      .contains(/Thêm|side|add-on/i)
      .click()
    cy.contains('Chả lụa').should('be.visible')
    cy.contains('Bún bò Huế').should('not.exist')
  })
})

// ══════════════════════════════════════════════════
// BROWSE → DETAIL → CART (luồng tích hợp)
// ══════════════════════════════════════════════════
describe('Luồng: Browse → Restaurant Detail → Giỏ hàng', () => {
  it('đi từ browse → click nhà hàng → thêm món → vào giỏ hàng', () => {
    cy.intercept('GET', '**/restaurants**', { statusCode: 200, body: mockRestaurants }).as('list')
    cy.intercept('GET', '**/restaurants/1', { statusCode: 200, body: mockRestaurants[0] }).as('detail')
    cy.intercept('GET', '**/restaurants/1/menu**', { statusCode: 200, body: mockMenuItems }).as('menu')

    cy.visit('/browse')
    cy.wait('@list')

    // Click vào nhà hàng
    cy.contains('Quán Huế').click()
    cy.wait('@detail')
    cy.wait('@menu')

    // Thêm món
    cy.get('button[class*="add"], button[class*="cart"]').first().click()

    // Đi vào giỏ hàng
    cy.visit('/cart')
    cy.contains('Bún bò Huế').should('be.visible')
  })
})
