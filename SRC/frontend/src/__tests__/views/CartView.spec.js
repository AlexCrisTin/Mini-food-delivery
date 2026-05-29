/**
 * Component Tests: App.vue + CartView.vue
 * Dùng: Vitest + Vue Test Utils + Pinia
 */
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'

// ─── Mock router ───────────────────────────────
const mockRouter = createRouter({
  history: createMemoryHistory(),
  routes: [
    { path: '/', component: { template: '<div>Home</div>' } },
    { path: '/browse', component: { template: '<div>Browse</div>' } },
    { path: '/cart', component: { template: '<div>Cart</div>' } },
  ],
})

// ─── Mock các SVG / asset import ───────────────
vi.mock('@/assets/icon/back-arrow.svg', () => ({ default: '' }))
vi.mock('@/assets/icon/home.svg', () => ({ default: '' }))
vi.mock('@/assets/icon/image.svg', () => ({ default: '' }))
vi.mock('@/assets/icon/delete.svg', () => ({ default: '' }))

// ─── Mock useCartViewModel ──────────────────────
vi.mock('@/composables/useCartViewModel', () => ({
  useCartViewModel: vi.fn(),
}))

import { useCartViewModel } from '@/composables/useCartViewModel'
import CartView from '@/views/user/CartView.vue'

// ─── Helpers ────────────────────────────────────
function buildCartViewModel(overrides = {}) {
  return {
    cartItems: [],
    subtotal: 0,
    deliveryFee: 0,
    discount: 0,
    total: 0,
    formatPrice: (v) => `${v.toLocaleString('vi-VN')} ₫`,
    groupedByRestaurant: {},
    increment: vi.fn(),
    decrement: vi.fn(),
    removeItem: vi.fn(),
    goBrowse: vi.fn(),
    ...overrides,
  }
}

const mockItem = {
  lineId: 'abc::vua::',
  id: 1,
  name: 'Bún bò Huế',
  price: 45000,
  quantity: 2,
  restaurantId: 10,
  restaurantName: 'Quán Huế',
  size: 'Vừa',
  note: '',
  imageUrl: null,
}

// ═══════════════════════════════════════════════
// App.vue
// ═══════════════════════════════════════════════
import App from '@/App.vue'

describe('App.vue', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('render được mà không bị lỗi', async () => {
    const wrapper = mount(App, {
      global: {
        plugins: [mockRouter],
        stubs: { RouterView: { template: '<div class="router-view-stub" />' } },
      },
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('chứa <RouterView> (hoặc stub)', async () => {
    const wrapper = mount(App, {
      global: {
        plugins: [mockRouter],
        stubs: { RouterView: { template: '<div class="router-view-stub" />' } },
      },
    })
    expect(wrapper.find('.router-view-stub').exists()).toBe(true)
  })
})

// ═══════════════════════════════════════════════
// CartView.vue
// ═══════════════════════════════════════════════
describe('CartView.vue – giỏ hàng rỗng', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    useCartViewModel.mockReturnValue(buildCartViewModel())
  })

  it('hiển thị tiêu đề "Giỏ hàng"', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).toContain('Giỏ hàng')
  })

  it('hiển thị trạng thái empty khi giỏ hàng rỗng', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).toContain('Giỏ hàng trống')
  })

  it('hiển thị nút "Khám phá ngay" khi giỏ rỗng', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.find('button.browse-btn').exists()).toBe(true)
  })

  it('click "Khám phá ngay" gọi goBrowse()', async () => {
    const goBrowse = vi.fn()
    useCartViewModel.mockReturnValue(buildCartViewModel({ goBrowse }))
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    await wrapper.find('button.browse-btn').trigger('click')
    expect(goBrowse).toHaveBeenCalledOnce()
  })
})

describe('CartView.vue – có món trong giỏ', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    useCartViewModel.mockReturnValue(
      buildCartViewModel({
        cartItems: [mockItem],
        subtotal: 90000,
        deliveryFee: 18000,
        discount: 0,
        total: 108000,
        groupedByRestaurant: { 'Quán Huế': [mockItem] },
      }),
    )
  })

  it('hiển thị tên món trong giỏ hàng', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).toContain('Bún bò Huế')
  })

  it('hiển thị tên nhà hàng', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).toContain('Quán Huế')
  })

  it('hiển thị số lượng = 2', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).toContain('2')
  })

  it('không hiển thị "Giỏ hàng trống" khi có món', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    expect(wrapper.text()).not.toContain('Giỏ hàng trống')
  })

  it('click nút + gọi increment(item)', async () => {
    const increment = vi.fn()
    useCartViewModel.mockReturnValue(
      buildCartViewModel({
        cartItems: [mockItem],
        groupedByRestaurant: { 'Quán Huế': [mockItem] },
        increment,
      }),
    )
    const wrapper = mount(CartView, { global: { plugins: [mockRouter] } })
    const qtyBtns = wrapper.findAll('button.qty-btn')
    const plusBtn = qtyBtns.find((b) => b.text() === '+')
    await plusBtn?.trigger('click')
    expect(increment).toHaveBeenCalledWith(mockItem)
  })

  it('click nút xóa gọi removeItem(lineId)', async () => {
    const removeItem = vi.fn()
    useCartViewModel.mockReturnValue(
      buildCartViewModel({
        cartItems: [mockItem],
        groupedByRestaurant: { 'Quán Huế': [mockItem] },
        removeItem,
      }),
    )
    const wrapper = mount(CartView, { global: { plugins: [mockRouter] } })
    await wrapper.find('button.remove-btn').trigger('click')
    expect(removeItem).toHaveBeenCalledWith(mockItem.lineId)
  })

  it('có nút quay lại "/browse"', () => {
    const wrapper = mount(CartView, {
      global: { plugins: [mockRouter] },
    })
    const backLink = wrapper.find('a.back-btn, .back-btn')
    expect(backLink.exists()).toBe(true)
  })
})
