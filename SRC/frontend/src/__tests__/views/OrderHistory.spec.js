import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import OrderHistory from '@/views/user/OrderHistory.vue'
import { useOrderStore } from '@/stores/order'

vi.mock('@/assets/icon/back-arrow.svg', () => ({ default: '' }))

vi.mock('@/services/orderService', () => ({
  default: {
    getByUser: vi.fn().mockResolvedValue([]),
    getById: vi.fn(),
    create: vi.fn(),
    cancel: vi.fn(),
  },
}))

const router = createRouter({
  history: createMemoryHistory(),
  routes: [
    { path: '/orders/history', component: OrderHistory },
    { path: '/profile', component: { template: '<div>Profile</div>' } },
    { path: '/orders/:id/tracking', component: { template: '<div />' } },
  ],
})

describe('OrderHistory.vue', () => {
  beforeEach(async () => {
    setActivePinia(createPinia())
    await router.push('/orders/history')
    await router.isReady()
    vi.clearAllMocks()
  })

  async function mountPage(orders = []) {
    const orderStore = useOrderStore()
    vi.spyOn(orderStore, 'fetchUserOrders').mockImplementation(async () => {
      orderStore.orders = orders
      return orders
    })
    const wrapper = mount(OrderHistory, {
      global: { plugins: [router] },
    })
    await flushPromises()
    return wrapper
  }

  it('hiển thị tiêu đề trang', async () => {
    const wrapper = await mountPage()
    expect(wrapper.text()).toContain('Lịch sử đơn hàng')
  })

  it('danh sách rỗng → thông báo chưa có đơn', async () => {
    const wrapper = await mountPage([])
    expect(wrapper.text()).toContain('Chưa có đơn hàng nào')
  })

  it('hiển thị đơn hàng từ store', async () => {
    const wrapper = await mountPage([
      {
        id: 42,
        status: 'DELIVERED',
        totalAmount: 99000,
        restaurantName: 'Quán Huế',
        createdAt: '2025-06-01T10:00:00',
      },
    ])
    expect(wrapper.text()).toContain('#42')
    expect(wrapper.text()).toContain('Quán Huế')
    expect(wrapper.text()).toContain('Theo dõi đơn')
  })
})
