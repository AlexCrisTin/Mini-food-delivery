import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCartStore } from '@/stores/cart'

// Mock window.confirm để tránh dialog thật
vi.stubGlobal('confirm', vi.fn(() => true))

// Mock localStorage
const localStorageMock = (() => {
  let store = {}
  return {
    getItem: (key) => store[key] ?? null,
    setItem: (key, value) => { store[key] = String(value) },
    removeItem: (key) => { delete store[key] },
    clear: () => { store = {} },
  }
})()
vi.stubGlobal('localStorage', localStorageMock)

// ── Dữ liệu mẫu ────────────────────────────────
const mockItem = {
  id: 1,
  name: 'Bún bò Huế',
  price: 45000,
  restaurantId: 10,
  restaurantName: 'Quán Huế',
  size: 'Vừa',
  note: '',
  imageUrl: null,
}

const mockItem2 = {
  id: 2,
  name: 'Cơm tấm',
  price: 35000,
  restaurantId: 10,
  restaurantName: 'Quán Huế',
  size: 'Vừa',
  note: '',
  imageUrl: null,
}

const mockItemOtherRestaurant = {
  id: 3,
  name: 'Phở bò',
  price: 50000,
  restaurantId: 99,
  restaurantName: 'Quán Phở',
  size: 'Lớn',
  note: '',
  imageUrl: null,
}

describe('CartStore – khởi tạo', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('giỏ hàng ban đầu rỗng', () => {
    const cart = useCartStore()
    expect(cart.items).toHaveLength(0)
  })

  it('itemCount ban đầu là 0', () => {
    const cart = useCartStore()
    expect(cart.itemCount).toBe(0)
  })

  it('subtotal ban đầu là 0', () => {
    const cart = useCartStore()
    expect(cart.subtotal).toBe(0)
  })

  it('note ban đầu là chuỗi rỗng', () => {
    const cart = useCartStore()
    expect(cart.note).toBe('')
  })
})

describe('CartStore – addItem', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('thêm 1 món vào giỏ hàng rỗng', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    expect(cart.items).toHaveLength(1)
    expect(cart.items[0].name).toBe('Bún bò Huế')
  })

  it('thêm cùng món → tăng số lượng thay vì tạo dòng mới', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.addItem(mockItem)
    expect(cart.items).toHaveLength(1)
    expect(cart.items[0].quantity).toBe(2)
  })

  it('thêm 2 món khác nhau cùng nhà hàng → có 2 dòng', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.addItem(mockItem2)
    expect(cart.items).toHaveLength(2)
  })

  it('thêm số lượng cụ thể (qty = 3)', () => {
    const cart = useCartStore()
    cart.addItem(mockItem, 3)
    expect(cart.items[0].quantity).toBe(3)
  })

  it('thêm món từ nhà hàng khác và xác nhận → xóa giỏ cũ, thêm món mới', () => {
    vi.mocked(window.confirm).mockReturnValueOnce(true)
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.addItem(mockItemOtherRestaurant)
    expect(cart.items).toHaveLength(1)
    expect(cart.items[0].restaurantId).toBe(99)
  })

  it('thêm món từ nhà hàng khác và từ chối → giỏ hàng không đổi', () => {
    vi.mocked(window.confirm).mockReturnValueOnce(false)
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.addItem(mockItemOtherRestaurant)
    expect(cart.items).toHaveLength(1)
    expect(cart.items[0].restaurantId).toBe(10)
  })
})

describe('CartStore – removeItem', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('xóa món theo lineId → giỏ hàng rỗng', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    const lineId = cart.items[0].lineId
    cart.removeItem(lineId)
    expect(cart.items).toHaveLength(0)
  })

  it('xóa lineId không tồn tại → không có lỗi', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.removeItem('fake-line-id')
    expect(cart.items).toHaveLength(1)
  })
})

describe('CartStore – updateQuantity', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('cập nhật số lượng hợp lệ', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    const lineId = cart.items[0].lineId
    cart.updateQuantity(lineId, 5)
    expect(cart.items[0].quantity).toBe(5)
  })

  it('số lượng = 0 → xóa khỏi giỏ', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    const lineId = cart.items[0].lineId
    cart.updateQuantity(lineId, 0)
    expect(cart.items).toHaveLength(0)
  })

  it('số lượng âm → xóa khỏi giỏ', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    const lineId = cart.items[0].lineId
    cart.updateQuantity(lineId, -1)
    expect(cart.items).toHaveLength(0)
  })
})

describe('CartStore – clearCart', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('xóa toàn bộ giỏ hàng', () => {
    const cart = useCartStore()
    cart.addItem(mockItem)
    cart.addItem(mockItem2)
    cart.clearCart()
    expect(cart.items).toHaveLength(0)
  })

  it('xóa note khi clearCart', () => {
    const cart = useCartStore()
    cart.setNote('Ghi chú test')
    cart.clearCart()
    expect(cart.note).toBe('')
  })
})

describe('CartStore – computed (itemCount, subtotal)', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('itemCount đếm đúng tổng số lượng tất cả dòng', () => {
    const cart = useCartStore()
    cart.addItem(mockItem, 2)
    cart.addItem(mockItem2, 3)
    expect(cart.itemCount).toBe(5)
  })

  it('subtotal tính đúng tổng tiền', () => {
    const cart = useCartStore()
    cart.addItem(mockItem, 1)    // 45.000 ₫
    cart.addItem(mockItem2, 2)   // 70.000 ₫
    expect(cart.subtotal).toBe(115000)
  })

  it('subtotal = 0 khi giỏ hàng rỗng', () => {
    const cart = useCartStore()
    expect(cart.subtotal).toBe(0)
  })
})

describe('CartStore – setNote', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('lưu ghi chú đơn hàng', () => {
    const cart = useCartStore()
    cart.setNote('Không cay')
    expect(cart.note).toBe('Không cay')
  })

  it('cập nhật ghi chú nhiều lần', () => {
    const cart = useCartStore()
    cart.setNote('Lần 1')
    cart.setNote('Lần 2')
    expect(cart.note).toBe('Lần 2')
  })
})
