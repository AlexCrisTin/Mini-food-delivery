import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

// ── Mock authService ────────────────────────────
vi.mock('@/services/authService', () => ({
  default: {
    login: vi.fn(),
    register: vi.fn(),
    getProfile: vi.fn(),
    logout: vi.fn(),
  },
}))

// ── Mock cartStore ──────────────────────────────
vi.mock('@/stores/cart', () => ({
  useCartStore: vi.fn(() => ({
    setUser: vi.fn(),
    clearCart: vi.fn(),
  })),
}))

// ── Mock localStorage ───────────────────────────
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

import authService from '@/services/authService'

const mockUserCustomer = {
  id: 1,
  email: 'customer@test.com',
  role: 'ROLE_CUSTOMER',
  fullName: 'Nguyễn Văn A',
}
const mockUserAdmin = {
  id: 2,
  email: 'admin@test.com',
  role: 'ADMIN',
  fullName: 'Admin',
}

describe('AuthStore – trạng thái ban đầu', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
  })

  it('user ban đầu là null', () => {
    const auth = useAuthStore()
    expect(auth.user).toBeNull()
  })

  it('isAuthenticated = false khi chưa có token', () => {
    const auth = useAuthStore()
    expect(auth.isAuthenticated).toBe(false)
  })

  it('userRole = null khi chưa đăng nhập', () => {
    const auth = useAuthStore()
    expect(auth.userRole).toBeNull()
  })

  it('isLoading = false khi khởi tạo', () => {
    const auth = useAuthStore()
    expect(auth.isLoading).toBe(false)
  })

  it('error = null khi khởi tạo', () => {
    const auth = useAuthStore()
    expect(auth.error).toBeNull()
  })
})

describe('AuthStore – login', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('đăng nhập thành công → lưu user và token', async () => {
    authService.login.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'mock-jwt-token',
    })
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    expect(auth.user).not.toBeNull()
    expect(auth.token).toBe('mock-jwt-token')
  })

  it('đăng nhập thành công → isAuthenticated = true', async () => {
    authService.login.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'mock-jwt-token',
    })
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    expect(auth.isAuthenticated).toBe(true)
  })

  it('đăng nhập thành công → token lưu vào localStorage', async () => {
    authService.login.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'mock-jwt-token',
    })
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    expect(localStorageMock.getItem('token')).toBe('mock-jwt-token')
  })

  it('chuẩn hóa role: ROLE_CUSTOMER → CUSTOMER', async () => {
    authService.login.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'token',
    })
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    expect(auth.userRole).toBe('CUSTOMER')
  })

  it('chuẩn hóa role: ADMIN (không có prefix) → ADMIN', async () => {
    authService.login.mockResolvedValueOnce({
      user: mockUserAdmin,
      token: 'admin-token',
    })
    const auth = useAuthStore()
    await auth.login('admin@test.com', 'adminpass')
    expect(auth.userRole).toBe('ADMIN')
  })

  it('đăng nhập thất bại → lưu error message', async () => {
    authService.login.mockRejectedValueOnce(new Error('Sai mật khẩu'))
    const auth = useAuthStore()
    await expect(auth.login('wrong@test.com', 'wrong')).rejects.toThrow()
    expect(auth.error).toBe('Sai mật khẩu')
  })

  it('đăng nhập thất bại → isAuthenticated = false', async () => {
    authService.login.mockRejectedValueOnce(new Error('Unauthorized'))
    const auth = useAuthStore()
    try { await auth.login('bad@test.com', 'bad') } catch {}
    expect(auth.isAuthenticated).toBe(false)
  })

  it('isLoading = false sau khi login hoàn tất (dù thành công hay thất bại)', async () => {
    authService.login.mockResolvedValueOnce({ user: mockUserCustomer, token: 'token' })
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    expect(auth.isLoading).toBe(false)
  })
})

describe('AuthStore – register', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('đăng ký thành công → lưu user và token', async () => {
    authService.register.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'new-token',
    })
    const auth = useAuthStore()
    await auth.register({ fullName: 'Nguyễn Văn A', email: 'customer@test.com', password: '123456' })
    expect(auth.user).not.toBeNull()
    expect(auth.token).toBe('new-token')
  })

  it('đăng ký thành công → isAuthenticated = true', async () => {
    authService.register.mockResolvedValueOnce({
      user: mockUserCustomer,
      token: 'new-token',
    })
    const auth = useAuthStore()
    await auth.register({ fullName: 'Test', email: 'test@test.com', password: 'pass' })
    expect(auth.isAuthenticated).toBe(true)
  })

  it('đăng ký thất bại → error được lưu', async () => {
    authService.register.mockRejectedValueOnce(new Error('Email đã tồn tại'))
    const auth = useAuthStore()
    try { await auth.register({ email: 'dup@test.com', password: '123' }) } catch {}
    expect(auth.error).toBe('Email đã tồn tại')
  })
})

describe('AuthStore – logout', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('logout → user = null', async () => {
    authService.login.mockResolvedValueOnce({ user: mockUserCustomer, token: 'token' })
    authService.logout.mockResolvedValueOnce()
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    await auth.logout()
    expect(auth.user).toBeNull()
  })

  it('logout → token = null', async () => {
    authService.login.mockResolvedValueOnce({ user: mockUserCustomer, token: 'token' })
    authService.logout.mockResolvedValueOnce()
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    await auth.logout()
    expect(auth.token).toBeNull()
  })

  it('logout → isAuthenticated = false', async () => {
    authService.login.mockResolvedValueOnce({ user: mockUserCustomer, token: 'token' })
    authService.logout.mockResolvedValueOnce()
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    await auth.logout()
    expect(auth.isAuthenticated).toBe(false)
  })

  it('logout → token bị xóa khỏi localStorage', async () => {
    authService.login.mockResolvedValueOnce({ user: mockUserCustomer, token: 'token' })
    authService.logout.mockResolvedValueOnce()
    const auth = useAuthStore()
    await auth.login('customer@test.com', 'password123')
    await auth.logout()
    expect(localStorageMock.getItem('token')).toBeNull()
  })
})

describe('AuthStore – fetchProfile', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('không gọi API nếu không có token', async () => {
    const auth = useAuthStore()
    const result = await auth.fetchProfile()
    expect(result).toBeNull()
    expect(authService.getProfile).not.toHaveBeenCalled()
  })

  it('fetchProfile thành công → cập nhật user', async () => {
    localStorageMock.setItem('token', 'existing-token')
    authService.getProfile.mockResolvedValueOnce(mockUserCustomer)
    const auth = useAuthStore()
    await auth.fetchProfile()
    expect(auth.user).not.toBeNull()
    expect(auth.user.email).toBe('customer@test.com')
  })
})
