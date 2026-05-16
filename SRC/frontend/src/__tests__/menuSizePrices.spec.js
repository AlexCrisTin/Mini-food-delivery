import { describe, it, expect } from 'vitest'
import {
  getAutoSizePrices,
  stripSizePriceMeta,
  encodeDescriptionWithSizePrices,
  parseDescriptionAndSizePrices,
} from '@/utils/menuSizePrices'

describe('getAutoSizePrices', () => {
  it('tính small/medium/large từ giá gốc', () => {
    const prices = getAutoSizePrices(100000)
    expect(prices.small).toBe(90000)
    expect(prices.medium).toBe(100000)
    expect(prices.large).toBe(120000)
  })
})

describe('stripSizePriceMeta', () => {
  it('loại bỏ metadata SIZE_PRICES khỏi mô tả', () => {
    const raw = 'Món ngon\n[SIZE_PRICES]{"small":1,"medium":2,"large":3}'
    expect(stripSizePriceMeta(raw)).toBe('Món ngon')
  })
})

describe('encodeDescriptionWithSizePrices', () => {
  it('ghép mô tả sạch với JSON size', () => {
    const result = encodeDescriptionWithSizePrices('Phở đặc biệt', {
      small: 40000,
      medium: 45000,
      large: 50000,
    })
    expect(result).toContain('Phở đặc biệt')
    expect(result).toContain('[SIZE_PRICES]')
    expect(result).toContain('"medium":45000')
  })
})

describe('parseDescriptionAndSizePrices', () => {
  it('parse metadata hợp lệ', () => {
    const desc = 'Bún\n[SIZE_PRICES]{"small":40,"medium":45,"large":50}'
    const { cleanDescription, prices } = parseDescriptionAndSizePrices(desc, 45000)
    expect(cleanDescription).toBe('Bún')
    expect(prices.medium).toBe(45)
  })

  it('mô tả không có metadata → dùng auto prices', () => {
    const { cleanDescription, prices } = parseDescriptionAndSizePrices('Chỉ text', 50000)
    expect(cleanDescription).toBe('Chỉ text')
    expect(prices.medium).toBe(50000)
  })

  it('JSON lỗi → fallback auto', () => {
    const { prices } = parseDescriptionAndSizePrices('[SIZE_PRICES]{bad}', 30000)
    expect(prices.medium).toBe(30000)
  })
})
