import { describe, it, expect, vi } from 'vitest'
import { ref } from 'vue'
import {
  goBackToBrowseAction,
  cancelEditingProfileAction,
  startEditingProfileAction,
  openRestaurantDashboardAction,
} from '@/utils/profileViewUtils'

describe('profileViewUtils', () => {
  it('goBackToBrowseAction → /browse', () => {
    const router = { push: vi.fn() }
    goBackToBrowseAction(router)
    expect(router.push).toHaveBeenCalledWith('/browse')
  })

  it('cancelEditingProfileAction tắt chế độ sửa', () => {
    const isEditing = ref(true)
    cancelEditingProfileAction(isEditing)
    expect(isEditing.value).toBe(false)
  })

  it('startEditingProfileAction copy dữ liệu profile vào form', () => {
    const form = ref({})
    const profile = ref({
      full_name: 'Nguyễn Văn A',
      email: 'a@test.com',
      phone: '0987654321',
    })
    const isEditing = ref(false)
    startEditingProfileAction(form, profile, isEditing)
    expect(form.value.full_name).toBe('Nguyễn Văn A')
    expect(isEditing.value).toBe(true)
  })

  it('nhà hàng chưa duyệt → không điều hướng dashboard', () => {
    const router = { push: vi.fn() }
    const restaurantMessage = ref('')
    const closeModal = vi.fn()
    openRestaurantDashboardAction(
      { id: 5, isApproved: false },
      restaurantMessage,
      closeModal,
      router,
    )
    expect(router.push).not.toHaveBeenCalled()
    expect(restaurantMessage.value).toContain('chưa được admin duyệt')
  })

  it('nhà hàng đã duyệt → mở dashboard', () => {
    const router = { push: vi.fn() }
    const restaurantMessage = ref('')
    openRestaurantDashboardAction(
      { id: 5, isApproved: true },
      restaurantMessage,
      vi.fn(),
      router,
    )
    expect(router.push).toHaveBeenCalledWith('/restaurant/dashboard?restaurantId=5')
  })
})
