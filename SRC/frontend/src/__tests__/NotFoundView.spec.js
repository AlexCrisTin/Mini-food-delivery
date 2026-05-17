import { describe, it, expect } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import NotFoundView from '@/views/NotFoundView.vue'

const router = createRouter({
  history: createMemoryHistory(),
  routes: [
    { path: '/', component: { template: '<div>Home</div>' } },
    { path: '/:pathMatch(.*)*', component: NotFoundView },
  ],
})

describe('NotFoundView.vue', () => {
  it('hiển thị mã 404 và thông báo', async () => {
    await router.push('/khong-ton-tai')
    await router.isReady()
    const wrapper = mount(NotFoundView, {
      global: { plugins: [router] },
    })
    expect(wrapper.text()).toContain('404')
    expect(wrapper.text()).toContain('Trang không tồn tại')
  })

  it('nút "Về trang chủ" điều hướng về /', async () => {
    await router.push('/abc')
    await router.isReady()
    const wrapper = mount(NotFoundView, {
      global: { plugins: [router] },
    })
    await wrapper.find('.nf-btn').trigger('click')
    await flushPromises()
    await router.isReady()
    expect(router.currentRoute.value.path).toBe('/')
  })
})
