import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import {
  formatPriceVND,
  loadCheckoutAddressesAction,
  submitCheckoutOrderAction,
} from '@/utils/checkoutViewUtils'

describe('formatPriceVND', () => {
  it('định dạng giá VND', () => {
    expect(formatPriceVND(18000)).toMatch(/18\.000/)
    expect(formatPriceVND(18000)).toMatch(/đ/)
  })
})

describe('loadCheckoutAddressesAction', () => {
  it('tải địa chỉ và chọn mặc định', async () => {
    const deliveryAddresses = ref([])
    const selectedAddressId = ref(null)
    const errorMessage = ref('')
    const userService = {
      getAddresses: vi.fn().mockResolvedValue([
        { id: 1, addressLine: 'A', isDefault: false },
        { id: 2, addressLine: 'B', isDefault: true },
      ]),
    }
    await loadCheckoutAddressesAction(
      userService,
      deliveryAddresses,
      selectedAddressId,
      errorMessage,
    )
    expect(deliveryAddresses.value).toHaveLength(2)
    expect(selectedAddressId.value).toBe(2)
  })

  it('lỗi API → ghi errorMessage', async () => {
    const errorMessage = ref('')
    const userService = {
      getAddresses: vi.fn().mockRejectedValue(new Error('Timeout')),
    }
    await loadCheckoutAddressesAction(
      userService,
      ref([]),
      ref(null),
      errorMessage,
    )
    expect(errorMessage.value).toBe('Timeout')
  })
})

describe('submitCheckoutOrderAction – validation', () => {
  let errorMessage
  let successMessage
  let cartItems
  let selectedAddress
  let isSubmitting
  let orderNote
  let orderStore
  let cartStore
  let router

  beforeEach(() => {
    errorMessage = ref('')
    successMessage = ref('')
    cartItems = ref([])
    selectedAddress = ref(null)
    isSubmitting = ref(false)
    orderNote = ref('')
    orderStore = { createOrder: vi.fn() }
    cartStore = { clearCart: vi.fn(), removeItem: vi.fn() }
    router = { push: vi.fn() }
  })

  function runSubmit(extra = {}) {
    return submitCheckoutOrderAction({
      errorMessage,
      successMessage,
      cartItems,
      selectedAddress,
      isSubmitting,
      orderNote,
      orderStore,
      cartStore,
      router,
      selectedOrderType: ref('Giao ngay'),
      desiredDeliveryTime: ref(''),
      ...extra,
    })
  }

  it('giỏ trống → không gọi createOrder', async () => {
    await runSubmit()
    expect(errorMessage.value).toBe('Giỏ hàng đang trống')
    expect(orderStore.createOrder).not.toHaveBeenCalled()
  })

  it('chưa chọn địa chỉ → báo lỗi', async () => {
    cartItems.value = [
      { id: 1, quantity: 1, restaurantId: 10, lineId: 'x', note: '' },
    ]
    await runSubmit()
    expect(errorMessage.value).toBe('Vui lòng chọn địa chỉ giao hàng')
  })

  it('giao hẹn giờ mà thiếu giờ → báo lỗi', async () => {
    cartItems.value = [
      { id: 1, quantity: 1, restaurantId: 10, lineId: 'x', note: '' },
    ]
    selectedAddress.value = { id: 1, addressLine: '123 ABC', latitude: 10, longitude: 106 }
    await runSubmit({
      selectedOrderType: ref('Giao hẹn giờ'),
      desiredDeliveryTime: ref(''),
    })
    expect(errorMessage.value).toBe('Vui lòng chọn giờ mong muốn để giao hàng')
  })

  it('đặt hàng thành công → clearCart và thông báo', async () => {
    cartItems.value = [
      { id: 1, quantity: 1, restaurantId: 10, lineId: 'x', note: '' },
    ]
    selectedAddress.value = { id: 1, addressLine: '123 ABC', latitude: 10, longitude: 106 }
    orderStore.createOrder.mockResolvedValue({ id: 99 })
    vi.useFakeTimers()
    await runSubmit()
    expect(successMessage.value).toBe('Đặt đơn thành công')
    expect(cartStore.clearCart).toHaveBeenCalled()
    vi.advanceTimersByTime(800)
    expect(router.push).toHaveBeenCalledWith('/browse?view=orders')
    vi.useRealTimers()
  })
})
