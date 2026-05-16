import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useOrderStore } from '@/stores/order'

vi.mock('@/services/orderService', () => ({
  default: {
    getByUser: vi.fn(),
    getById: vi.fn(),
    create: vi.fn(),
    cancel: vi.fn(),
  },
}))

import orderService from '@/services/orderService'

const mockOrder = {
  id: 101,
  status: 'PENDING',
  totalAmount: 108000,
  restaurantId: 10,
  restaurantName: 'Quán Huế',
}

describe('OrderStore – khởi tạo', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('orders ban đầu là mảng rỗng', () => {
    const order = useOrderStore()
    expect(order.orders).toEqual([])
  })

  it('currentOrder ban đầu là null', () => {
    const order = useOrderStore()
    expect(order.currentOrder).toBeNull()
  })

  it('isLoading = false khi không có request', () => {
    const order = useOrderStore()
    expect(order.isLoading).toBe(false)
  })
})

describe('OrderStore – fetchUserOrders', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('tải danh sách đơn thành công', async () => {
    orderService.getByUser.mockResolvedValueOnce([mockOrder])
    const order = useOrderStore()
    const data = await order.fetchUserOrders()
    expect(data).toHaveLength(1)
    expect(order.orders[0].id).toBe(101)
  })

  it('thất bại → lưu error', async () => {
    orderService.getByUser.mockRejectedValueOnce(new Error('Mạng lỗi'))
    const order = useOrderStore()
    await expect(order.fetchUserOrders()).rejects.toThrow()
    expect(order.error).toBe('Mạng lỗi')
  })

  it('isLoading về false sau khi hoàn tất', async () => {
    orderService.getByUser.mockResolvedValueOnce([])
    const order = useOrderStore()
    await order.fetchUserOrders()
    expect(order.isLoading).toBe(false)
  })
})

describe('OrderStore – activeOrders', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('lọc đơn đang xử lý (PENDING, SHIPPING...)', () => {
    const order = useOrderStore()
    order.orders = [
      { id: 1, status: 'PENDING' },
      { id: 2, status: 'DELIVERED' },
      { id: 3, status: 'SHIPPING' },
    ]
    expect(order.activeOrders).toHaveLength(2)
    expect(order.activeOrders.map((o) => o.id)).toEqual([1, 3])
  })
})

describe('OrderStore – createOrder', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('tạo đơn → thêm đầu danh sách và set currentOrder', async () => {
    const created = { ...mockOrder, id: 200 }
    orderService.create.mockResolvedValueOnce(created)
    const order = useOrderStore()
    const result = await order.createOrder({ restaurantId: 10 })
    expect(result.id).toBe(200)
    expect(order.orders[0].id).toBe(200)
    expect(order.currentOrder?.id).toBe(200)
  })
})

describe('OrderStore – cancelOrder', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('hủy đơn → cập nhật status trong orders và currentOrder', async () => {
    orderService.cancel.mockResolvedValueOnce({ status: 'CANCELLED' })
    const order = useOrderStore()
    order.orders = [{ id: 101, status: 'PENDING' }]
    order.currentOrder = { id: 101, status: 'PENDING' }
    await order.cancelOrder(101)
    expect(order.orders[0].status).toBe('CANCELLED')
    expect(order.currentOrder.status).toBe('CANCELLED')
  })
})
