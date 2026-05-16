import { describe, it, expect, vi } from 'vitest'
import { ref } from 'vue'
import {
  openLoginModalAction,
  closeLoginModalAction,
  showRegisterPanelAction,
  onRegisterSubmitAction,
  onLoginSubmitAction,
  onKeydownEscapeAction,
} from '@/utils/homeViewUtils'

describe('modal actions', () => {
  it('openLoginModalAction mở tab đăng nhập', () => {
    const authTab = ref('register')
    const loginOpen = ref(false)
    openLoginModalAction(authTab, loginOpen)
    expect(authTab.value).toBe('login')
    expect(loginOpen.value).toBe(true)
  })

  it('closeLoginModalAction đóng modal', () => {
    const loginOpen = ref(true)
    closeLoginModalAction(loginOpen)
    expect(loginOpen.value).toBe(false)
  })

  it('showRegisterPanelAction chuyển tab và xóa lỗi', () => {
    const authError = ref('lỗi cũ')
    const authTab = ref('login')
    showRegisterPanelAction(authError, authTab)
    expect(authTab.value).toBe('register')
    expect(authError.value).toBe('')
  })
})

describe('onRegisterSubmitAction – validation', () => {
  it('thiếu trường → không gọi register', async () => {
    const authStore = { register: vi.fn() }
    const authError = ref('')
    await onRegisterSubmitAction({
      authError,
      regFullName: ref(''),
      regEmail: ref('a@b.com'),
      regPassword: ref('12345678'),
      regConfirm: ref('12345678'),
      authStore,
      closeLoginModal: vi.fn(),
      router: { push: vi.fn() },
    })
    expect(authError.value).toContain('day du')
    expect(authStore.register).not.toHaveBeenCalled()
  })

  it('mật khẩu < 8 ký tự → báo lỗi', async () => {
    const authError = ref('')
    await onRegisterSubmitAction({
      authError,
      regFullName: ref('Test'),
      regEmail: ref('t@t.com'),
      regPassword: ref('123'),
      regConfirm: ref('123'),
      authStore: { register: vi.fn() },
      closeLoginModal: vi.fn(),
      router: { push: vi.fn() },
    })
    expect(authError.value).toContain('8 ky tu')
  })

  it('mật khẩu không khớp → báo lỗi', async () => {
    const authError = ref('')
    await onRegisterSubmitAction({
      authError,
      regFullName: ref('Test'),
      regEmail: ref('t@t.com'),
      regPassword: ref('12345678'),
      regConfirm: ref('87654321'),
      authStore: { register: vi.fn() },
      closeLoginModal: vi.fn(),
      router: { push: vi.fn() },
    })
    expect(authError.value).toContain('khong khop')
  })
})

describe('onLoginSubmitAction – điều hướng theo role', () => {
  it('CUSTOMER → /browse', async () => {
    const router = { push: vi.fn() }
    const authStore = {
      login: vi.fn().mockResolvedValue({ role: 'ROLE_CUSTOMER' }),
    }
    await onLoginSubmitAction({
      authError: ref(''),
      authStore,
      loginEmail: ref('u@t.com'),
      loginPassword: ref('pass'),
      closeLoginModal: vi.fn(),
      router,
    })
    expect(router.push).toHaveBeenCalledWith('/browse')
  })

  it('ADMIN → /admin/dashboard', async () => {
    const router = { push: vi.fn() }
    await onLoginSubmitAction({
      authError: ref(''),
      authStore: { login: vi.fn().mockResolvedValue({ role: 'ADMIN' }) },
      loginEmail: ref('a@a.com'),
      loginPassword: ref('pass'),
      closeLoginModal: vi.fn(),
      router,
    })
    expect(router.push).toHaveBeenCalledWith('/admin/dashboard')
  })

  it('đăng nhập thất bại → hiện lỗi', async () => {
    const authError = ref('')
    await onLoginSubmitAction({
      authError,
      authStore: { login: vi.fn().mockRejectedValue(new Error('Sai MK')) },
      loginEmail: ref('u@t.com'),
      loginPassword: ref('wrong'),
      closeLoginModal: vi.fn(),
      router: { push: vi.fn() },
    })
    expect(authError.value).toBe('Sai MK')
  })
})

describe('onKeydownEscapeAction', () => {
  it('Escape khi modal mở → gọi close', () => {
    const close = vi.fn()
    onKeydownEscapeAction({ key: 'Escape' }, ref(true), close)
    expect(close).toHaveBeenCalled()
  })

  it('phím khác → không đóng', () => {
    const close = vi.fn()
    onKeydownEscapeAction({ key: 'Enter' }, ref(true), close)
    expect(close).not.toHaveBeenCalled()
  })
})
