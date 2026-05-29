import { describe, it, expect } from 'vitest'
import {
  formatCurrency,
  formatDateTime,
  formatOrderStatus,
} from '@/utils/formatters'

describe('formatCurrency', () => {
  it('định dạng số thành tiền VND đúng chuẩn', () => {
    const result = formatCurrency(50000)
    expect(result).toMatch(/50\.000/)
    expect(result).toMatch(/₫|VND|đ/i)
  })

  it('trả về 0 ₫ khi giá trị là 0', () => {
    const result = formatCurrency(0)
    expect(result).toMatch(/0/)
  })

  it('xử lý giá trị null → trả về 0', () => {
    const result = formatCurrency(null)
    expect(result).toMatch(/0/)
  })

  it('xử lý giá trị undefined → trả về 0', () => {
    const result = formatCurrency(undefined)
    expect(result).toMatch(/0/)
  })

  it('định dạng số lớn đúng dấu phân cách nghìn', () => {
    const result = formatCurrency(1000000)
    expect(result).toMatch(/1\.000\.000/)
  })

  it('không hiển thị phần thập phân (maximumFractionDigits = 0)', () => {
    const result = formatCurrency(12500)
    expect(result).not.toMatch(/,\d{2}/)
  })
})

describe('formatDateTime', () => {
  it('định dạng chuỗi ISO thành ngày giờ tiếng Việt', () => {
    const result = formatDateTime('2024-06-15T08:30:00')
    expect(result).toMatch(/15/)
    expect(result).toMatch(/06/)
    expect(result).toMatch(/2024/)
    expect(result).toMatch(/08|8/)
    expect(result).toMatch(/30/)
  })

  it('trả về chuỗi rỗng khi value là null', () => {
    expect(formatDateTime(null)).toBe('')
  })

  it('trả về chuỗi rỗng khi value là undefined', () => {
    expect(formatDateTime(undefined)).toBe('')
  })

  it('trả về chuỗi rỗng khi value là chuỗi rỗng', () => {
    expect(formatDateTime('')).toBe('')
  })

  it('xử lý đối tượng Date hợp lệ', () => {
    const date = new Date('2024-01-01T12:00:00')
    const result = formatDateTime(date)
    expect(result).toMatch(/01/)
    expect(result).toMatch(/2024/)
  })
})

describe('formatOrderStatus', () => {
  it('PENDING → "Cho xac nhan"', () => {
    expect(formatOrderStatus('PENDING')).toBe('Cho xac nhan')
  })

  it('CONFIRMED → "Da xac nhan"', () => {
    expect(formatOrderStatus('CONFIRMED')).toBe('Da xac nhan')
  })

  it('PREPARING → "Dang chuan bi"', () => {
    expect(formatOrderStatus('PREPARING')).toBe('Dang chuan bi')
  })

  it('READY → "San sang giao"', () => {
    expect(formatOrderStatus('READY')).toBe('San sang giao')
  })

  it('SHIPPING → "Dang giao"', () => {
    expect(formatOrderStatus('SHIPPING')).toBe('Dang giao')
  })

  it('DELIVERED → "Da giao"', () => {
    expect(formatOrderStatus('DELIVERED')).toBe('Da giao')
  })

  it('CANCELLED → "Da huy"', () => {
    expect(formatOrderStatus('CANCELLED')).toBe('Da huy')
  })

  it('trạng thái không xác định → trả về nguyên gốc', () => {
    expect(formatOrderStatus('UNKNOWN_STATUS')).toBe('UNKNOWN_STATUS')
  })

  it('trạng thái rỗng → trả về chuỗi rỗng', () => {
    expect(formatOrderStatus('')).toBe('')
  })

  it('trạng thái null → trả về null', () => {
    expect(formatOrderStatus(null)).toBe(null)
  })
})
