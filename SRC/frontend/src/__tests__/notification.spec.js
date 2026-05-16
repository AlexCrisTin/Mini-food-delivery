import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useNotificationStore } from '@/stores/notification'

describe('NotificationStore', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('ban đầu không có thông báo', () => {
    const store = useNotificationStore()
    expect(store.items).toHaveLength(0)
    expect(store.unreadCount).toBe(0)
  })

  it('pushNotification → thêm vào đầu danh sách', () => {
    const store = useNotificationStore()
    const n = store.pushNotification({
      type: 'ORDER',
      title: 'Đơn mới',
      message: 'Đơn #1 đã được xác nhận',
    })
    expect(store.items).toHaveLength(1)
    expect(n.title).toBe('Đơn mới')
    expect(n.isRead).toBe(false)
    expect(store.unreadCount).toBe(1)
  })

  it('markAsRead → giảm unreadCount', () => {
    const store = useNotificationStore()
    const a = store.pushNotification({ title: 'A', message: '1' })
    store.pushNotification({ title: 'B', message: '2' })
    store.markAsRead(a.id)
    expect(store.unreadCount).toBe(1)
    expect(store.items.find((i) => i.id === a.id)?.isRead).toBe(true)
  })

  it('markAllAsRead → unreadCount = 0', () => {
    const store = useNotificationStore()
    store.pushNotification({ title: 'A', message: '1' })
    store.pushNotification({ title: 'B', message: '2' })
    store.markAllAsRead()
    expect(store.unreadCount).toBe(0)
    expect(store.items.every((i) => i.isRead)).toBe(true)
  })

  it('clearNotifications → xóa toàn bộ', () => {
    const store = useNotificationStore()
    store.pushNotification({ title: 'A', message: '1' })
    store.clearNotifications()
    expect(store.items).toHaveLength(0)
  })

  it('giá trị mặc định khi payload thiếu trường', () => {
    const store = useNotificationStore()
    const n = store.pushNotification({})
    expect(n.type).toBe('SYSTEM')
    expect(n.title).toBe('Thong bao')
    expect(n.message).toBe('')
  })
})
