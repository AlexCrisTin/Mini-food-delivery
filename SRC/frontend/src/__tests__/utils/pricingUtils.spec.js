import { describe, it, expect } from 'vitest'
import { getDeliveryFeeBySubtotal, getDiscountBySubtotal } from '@/utils/pricingUtils'
import {
  DELIVERY_FEE_VND,
  DISCOUNT_AMOUNT_VND,
  DISCOUNT_THRESHOLD_VND,
} from '@/config/pricing'

// ──────────────────────────────────────────────
// Kiểm thử: getDeliveryFeeBySubtotal
// DELIVERY_FEE_VND = 18_000 ₫
// ──────────────────────────────────────────────
describe('getDeliveryFeeBySubtotal', () => {
  it('trả về phí giao hàng khi subtotal > 0', () => {
    expect(getDeliveryFeeBySubtotal(50000)).toBe(DELIVERY_FEE_VND)
  })

  it('trả về 0 khi subtotal = 0 (giỏ hàng rỗng)', () => {
    expect(getDeliveryFeeBySubtotal(0)).toBe(0)
  })

  it('trả về 0 khi subtotal không truyền vào (default 0)', () => {
    expect(getDeliveryFeeBySubtotal()).toBe(0)
  })

  it('trả về 0 khi subtotal là null', () => {
    expect(getDeliveryFeeBySubtotal(null)).toBe(0)
  })

  it('trả về phí giao hàng khi subtotal = 1 (biên dưới > 0)', () => {
    expect(getDeliveryFeeBySubtotal(1)).toBe(DELIVERY_FEE_VND)
  })

  it('phí giao hàng đúng bằng hằng số DELIVERY_FEE_VND = 18000', () => {
    expect(DELIVERY_FEE_VND).toBe(18000)
    expect(getDeliveryFeeBySubtotal(100000)).toBe(18000)
  })

  it('xử lý subtotal kiểu string số', () => {
    expect(getDeliveryFeeBySubtotal('60000')).toBe(DELIVERY_FEE_VND)
  })
})

// ──────────────────────────────────────────────
// Kiểm thử: getDiscountBySubtotal
// DISCOUNT_THRESHOLD_VND = 100_000 ₫
// DISCOUNT_AMOUNT_VND    = 20_000 ₫
// ──────────────────────────────────────────────
describe('getDiscountBySubtotal', () => {
  it('áp dụng giảm giá khi subtotal >= ngưỡng 100.000 ₫', () => {
    expect(getDiscountBySubtotal(100000)).toBe(DISCOUNT_AMOUNT_VND)
  })

  it('áp dụng giảm giá khi subtotal > ngưỡng', () => {
    expect(getDiscountBySubtotal(150000)).toBe(DISCOUNT_AMOUNT_VND)
  })

  it('không giảm giá khi subtotal < ngưỡng', () => {
    expect(getDiscountBySubtotal(99999)).toBe(0)
  })

  it('không giảm giá khi subtotal = 0', () => {
    expect(getDiscountBySubtotal(0)).toBe(0)
  })

  it('không giảm giá khi subtotal không truyền vào', () => {
    expect(getDiscountBySubtotal()).toBe(0)
  })

  it('mức giảm giá đúng bằng hằng số DISCOUNT_AMOUNT_VND = 20000', () => {
    expect(DISCOUNT_AMOUNT_VND).toBe(20000)
    expect(getDiscountBySubtotal(200000)).toBe(20000)
  })

  it('biên trên của vùng không giảm giá: 99.999 ₫ → 0', () => {
    expect(getDiscountBySubtotal(99999)).toBe(0)
  })

  it('biên dưới của vùng giảm giá: 100.000 ₫ → 20.000 ₫', () => {
    expect(getDiscountBySubtotal(DISCOUNT_THRESHOLD_VND)).toBe(DISCOUNT_AMOUNT_VND)
  })

  it('tổng tiền sau giảm phải nhỏ hơn subtotal ban đầu', () => {
    const subtotal = 120000
    const discount = getDiscountBySubtotal(subtotal)
    expect(subtotal - discount).toBeLessThan(subtotal)
  })
})

// ──────────────────────────────────────────────
// Kiểm thử: tích hợp tính tổng đơn hàng
// ──────────────────────────────────────────────
describe('Tính tổng đơn hàng (integration)', () => {
  function calcTotal(subtotal) {
    const delivery = getDeliveryFeeBySubtotal(subtotal)
    const discount = getDiscountBySubtotal(subtotal)
    return subtotal + delivery - discount
  }

  it('đơn hàng 50.000 ₫: tổng = 50.000 + 18.000 = 68.000 ₫', () => {
    expect(calcTotal(50000)).toBe(68000)
  })

  it('đơn hàng 100.000 ₫: tổng = 100.000 + 18.000 - 20.000 = 98.000 ₫', () => {
    expect(calcTotal(100000)).toBe(98000)
  })

  it('đơn hàng 200.000 ₫: tổng = 200.000 + 18.000 - 20.000 = 198.000 ₫', () => {
    expect(calcTotal(200000)).toBe(198000)
  })

  it('đơn hàng rỗng 0 ₫: tổng = 0', () => {
    expect(calcTotal(0)).toBe(0)
  })
})
