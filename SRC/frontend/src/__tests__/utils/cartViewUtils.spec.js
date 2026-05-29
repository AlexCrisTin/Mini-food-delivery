import { describe, it, expect, vi } from 'vitest'
import {
  formatCartPrice,
  groupCartByRestaurant,
  incrementCartItem,
  decrementCartItem,
  removeCartItem,
  goBrowseFromCart,
} from '@/utils/cartViewUtils'

describe('formatCartPrice', () => {
  it('định dạng số thành chuỗi VND', () => {
    expect(formatCartPrice(45000)).toMatch(/45\.000/)
    expect(formatCartPrice(45000)).toMatch(/đ/)
  })

  it('null/undefined → 0đ', () => {
    expect(formatCartPrice(null)).toMatch(/0/)
  })
})

describe('groupCartByRestaurant', () => {
  it('nhóm món theo tên nhà hàng', () => {
    const items = [
      { restaurantName: 'Quán A', name: 'Món 1' },
      { restaurantName: 'Quán A', name: 'Món 2' },
      { restaurantName: 'Quán B', name: 'Món 3' },
    ]
    const groups = groupCartByRestaurant(items)
    expect(groups['Quán A']).toHaveLength(2)
    expect(groups['Quán B']).toHaveLength(1)
  })

  it('thiếu restaurantName → dùng "Nhà hàng"', () => {
    const groups = groupCartByRestaurant([{ name: 'X' }])
    expect(groups['Nhà hàng']).toHaveLength(1)
  })
})

describe('cart item actions', () => {
  it('incrementCartItem gọi updateQuantity +1', () => {
    const cartStore = { updateQuantity: vi.fn() }
    const item = { lineId: 'a', quantity: 2 }
    incrementCartItem(cartStore, item)
    expect(cartStore.updateQuantity).toHaveBeenCalledWith('a', 3)
  })

  it('decrementCartItem gọi updateQuantity -1', () => {
    const cartStore = { updateQuantity: vi.fn() }
    decrementCartItem(cartStore, { lineId: 'b', quantity: 3 })
    expect(cartStore.updateQuantity).toHaveBeenCalledWith('b', 2)
  })

  it('removeCartItem gọi removeItem', () => {
    const cartStore = { removeItem: vi.fn() }
    removeCartItem(cartStore, 'line-1')
    expect(cartStore.removeItem).toHaveBeenCalledWith('line-1')
  })
})

describe('goBrowseFromCart', () => {
  it('điều hướng tới /browse', () => {
    const router = { push: vi.fn() }
    goBrowseFromCart(router)
    expect(router.push).toHaveBeenCalledWith('/browse')
  })
})
