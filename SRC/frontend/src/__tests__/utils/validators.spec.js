import { describe, it, expect } from 'vitest'
import { isRequired, isEmail, isPhoneVN, minLength } from '@/utils/validators'

describe('isRequired', () => {
  it('trả về true cho chuỗi hợp lệ', () => {
    expect(isRequired('hello')).toBe(true)
  })

  it('trả về false cho chuỗi rỗng', () => {
    expect(isRequired('')).toBe(false)
  })

  it('trả về false cho chuỗi chỉ có khoảng trắng', () => {
    expect(isRequired('   ')).toBe(false)
  })

  it('trả về false cho null', () => {
    expect(isRequired(null)).toBe(false)
  })

  it('trả về false cho undefined', () => {
    expect(isRequired(undefined)).toBe(false)
  })

  it('trả về true cho số 0 (truthy edge case)', () => {
    expect(isRequired(0)).toBe(true)
  })

  it('trả về true cho số dương', () => {
    expect(isRequired(42)).toBe(true)
  })
})

describe('isEmail', () => {
  it('trả về true cho email hợp lệ', () => {
    expect(isEmail('user@example.com')).toBe(true)
  })

  it('trả về true cho email có subdomain', () => {
    expect(isEmail('user@mail.example.co.uk')).toBe(true)
  })

  it('trả về false khi thiếu @', () => {
    expect(isEmail('userexample.com')).toBe(false)
  })

  it('trả về false khi thiếu domain sau @', () => {
    expect(isEmail('user@')).toBe(false)
  })

  it('trả về false khi thiếu TLD', () => {
    expect(isEmail('user@example')).toBe(false)
  })

  it('trả về false cho chuỗi rỗng', () => {
    expect(isEmail('')).toBe(false)
  })

  it('trả về false cho null', () => {
    expect(isEmail(null)).toBe(false)
  })

  it('trả về false khi có khoảng trắng trong email', () => {
    expect(isEmail('user @example.com')).toBe(false)
  })
})

describe('isPhoneVN', () => {
  it('trả về true cho số bắt đầu bằng 03x', () => {
    expect(isPhoneVN('0345678901')).toBe(true)
  })

  it('trả về true cho số bắt đầu bằng 09x', () => {
    expect(isPhoneVN('0987654321')).toBe(true)
  })

  it('trả về true cho số bắt đầu bằng +84', () => {
    expect(isPhoneVN('+84987654321')).toBe(true)
  })

  it('trả về true cho số bắt đầu bằng 07x', () => {
    expect(isPhoneVN('0712345678')).toBe(true)
  })

  it('trả về false cho số bắt đầu bằng 02x (Hà Nội cố định)', () => {
    expect(isPhoneVN('0212345678')).toBe(false)
  })

  it('trả về false cho số có ít hơn 10 chữ số', () => {
    expect(isPhoneVN('09876543')).toBe(false)
  })

  it('trả về false cho số có nhiều hơn 10 chữ số', () => {
    expect(isPhoneVN('098765432100')).toBe(false)
  })

  it('trả về false cho chuỗi rỗng', () => {
    expect(isPhoneVN('')).toBe(false)
  })

  it('trả về false cho null', () => {
    expect(isPhoneVN(null)).toBe(false)
  })

  it('trả về false cho số có chữ cái', () => {
    expect(isPhoneVN('09876abc21')).toBe(false)
  })
})

describe('minLength', () => {
  it('trả về true khi chuỗi dài hơn min', () => {
    expect(minLength('hello', 3)).toBe(true)
  })

  it('trả về true khi chuỗi đúng bằng min', () => {
    expect(minLength('abc', 3)).toBe(true)
  })

  it('trả về false khi chuỗi ngắn hơn min', () => {
    expect(minLength('ab', 3)).toBe(false)
  })

  it('trả về false cho chuỗi rỗng với min > 0', () => {
    expect(minLength('', 1)).toBe(false)
  })

  it('trả về true cho chuỗi rỗng với min = 0', () => {
    expect(minLength('', 0)).toBe(true)
  })

  it('cắt khoảng trắng trước khi kiểm tra', () => {
    expect(minLength('  ab  ', 3)).toBe(false)
  })

  it('xử lý null → length 0', () => {
    expect(minLength(null, 1)).toBe(false)
  })

  it('xử lý undefined → length 0', () => {
    expect(minLength(undefined, 1)).toBe(false)
  })

  it('mật khẩu 6 ký tự đạt minLength 6', () => {
    expect(minLength('pass12', 6)).toBe(true)
  })
})
