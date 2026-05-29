import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import {
  isItemFavorite,
  toggleFavoriteItem,
  mapBrowseCategory,
  getSizeAdjustedPrice,
  formatDishPrice,
  openDishDetailModal,
  closeDishDetailModal,
} from '@/utils/browseViewUtils'

const localStorageMock = (() => {
  let store = {}
  return {
    getItem: (key) => store[key] ?? null,
    setItem: (key, value) => { store[key] = String(value) },
    clear: () => { store = {} },
  }
})()
vi.stubGlobal('localStorage', localStorageMock)

describe('isItemFavorite / toggleFavoriteItem', () => {
  beforeEach(() => localStorageMock.clear())

  it('toggle thêm và xóa yêu thích', () => {
    const ids = ref([])
    const item = { id: 5, name: 'Phở' }
    toggleFavoriteItem(ids, item, 'test_fav')
    expect(isItemFavorite(ids, 5)).toBe(true)
    toggleFavoriteItem(ids, item, 'test_fav')
    expect(isItemFavorite(ids, 5)).toBe(false)
  })

  it('lưu vào localStorage', () => {
    const ids = ref([])
    toggleFavoriteItem(ids, { id: 1 }, 'test_fav')
    expect(JSON.parse(localStorageMock.getItem('test_fav'))).toEqual([1])
  })
})

describe('mapBrowseCategory', () => {
  it('gán ảnh phở cho danh mục có từ "phở"', () => {
    const result = mapBrowseCategory({ name: 'Phở bò' })
    expect(result.label).toBe('Phở bò')
    expect(result.image).toContain('unsplash')
  })

  it('dùng iconUrl nếu có', () => {
    const result = mapBrowseCategory({ name: 'Khác', iconUrl: 'https://cdn.test/a.jpg' })
    expect(result.image).toBe('https://cdn.test/a.jpg')
  })
})

describe('getSizeAdjustedPrice', () => {
  it('sizePrices → giá theo size', () => {
    const item = { sizePrices: { small: 40000, medium: 45000, large: 50000 } }
    expect(getSizeAdjustedPrice(item, 'Nhỏ')).toBe(40000)
    expect(getSizeAdjustedPrice(item, 'Lớn')).toBe(50000)
  })

  it('không có sizePrices → nhân hệ số Vừa/Lớn', () => {
    const item = { basePrice: 100000 }
    expect(getSizeAdjustedPrice(item, 'Vừa')).toBe(100000)
    expect(getSizeAdjustedPrice(item, 'Lớn')).toBe(120000)
  })
})

describe('formatDishPrice', () => {
  it('làm tròn và format VND', () => {
    expect(formatDishPrice(45500.7)).toMatch(/45\.501|45\.500/)
    expect(formatDishPrice(45500.7)).toMatch(/đ/)
  })
})

describe('dish detail modal', () => {
  it('open và close modal', () => {
    const selected = ref(null)
    const note = ref('old')
    const size = ref('Lớn')
    openDishDetailModal(selected, note, size, { id: 1, name: 'Bún' })
    expect(selected.value.name).toBe('Bún')
    expect(note.value).toBe('')
    expect(size.value).toBe('Vừa')
    closeDishDetailModal(selected)
    expect(selected.value).toBeNull()
  })
})
