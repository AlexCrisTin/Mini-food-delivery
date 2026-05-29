# Utilities Detailed Tests

This document specifies the individual test scenarios covered in the utility spec files (`src/__tests__/*utils.spec.js`, `validators.spec.js`, `formatters.spec.js`, `menuSizePrices.spec.js`, `pricingUtils.spec.js`).

---

## 1. Validators Unit Tests (`validators.spec.js`)

### `isRequired`
- **Positive Scenario (Valid string)**: `isRequired('hello')` returns `true`.
- **Negative Scenario (Empty / Whitespace)**: `isRequired('')`, `isRequired('   ')`, `isRequired(null)` return `false`.
- **Edge Case (Number zero)**: `isRequired(0)` returns `true` (truthy numeric).

### `isEmail`
- **Positive Scenario (Valid Email)**: `isEmail('user@example.com')`, `isEmail('user@mail.example.co.uk')` return `true`.
- **Negative Scenario (Malformed Emails)**: `isEmail('userexample.com')`, `isEmail('user@')`, `isEmail('user @example.com')` return `false`.

### `isPhoneVN`
- **Positive Scenario (Vietnamese Carriers)**: `isPhoneVN('0987654321')`, `isPhoneVN('0345678901')`, `isPhoneVN('+84987654321')` return `true`.
- **Negative Scenario (Invalid Prefixes/Lengths)**: `isPhoneVN('0212345678')` (fixed landline), `isPhoneVN('09876abc21')` (non-numeric), `isPhoneVN('09876543')` (too short) return `false`.

### `minLength`
- **Positive Scenario (Passes limit)**: `minLength('hello', 3)` returns `true`.
- **Negative Scenario (Under limit)**: `minLength('ab', 3)`, `minLength('  ab  ', 3)` (trimmed whitespace check) return `false`.

---

## 2. Formatters Unit Tests (`formatters.spec.js`)

### `formatCurrency`
- **Positive Scenario (VND formatting)**: `formatCurrency(50000)` returns `"50.000 ₫"` or equivalent localized spacing.
- **Negative Scenario (Null/Undefined parameters)**: `formatCurrency(null)` gracefully falls back to `"0"`.

### `formatDateTime`
- **Positive Scenario (ISO format string)**: `formatDateTime('2024-06-15T08:30:00')` outputs date in Vietnamese convention.
- **Negative Scenario (Empty parameters)**: `formatDateTime(null)`, `formatDateTime('')` return empty string `""`.

### `formatOrderStatus`
- **Positive Scenario (Status translation)**:
  - `PENDING` -> `"Cho xac nhan"`
  - `CONFIRMED` -> `"Da xac nhan"`
  - `PREPARING` -> `"Dang chuan bi"`
  - `READY` -> `"San sang giao"`
  - `SHIPPING` -> `"Dang giao"`
  - `DELIVERED` -> `"Da giao"`
  - `CANCELLED` -> `"Da huy"`
- **Fallback Scenario (Unknown key)**: `formatOrderStatus('UNKNOWN')` returns `'UNKNOWN'`.

---

## 3. Pricing Utilities Unit Tests (`pricingUtils.spec.js`)

### `getDeliveryFeeBySubtotal`
- **Standard delivery fee**: `getDeliveryFeeBySubtotal(50000)` returns `18000`.
- **Empty boundary fee**: `getDeliveryFeeBySubtotal(0)`, `getDeliveryFeeBySubtotal(null)` return `0`.

### `getDiscountBySubtotal`
- **Discount Threshold**:
  - `getDiscountBySubtotal(100000)` returns `20000` (equal to threshold `100_000 ₫`).
  - `getDiscountBySubtotal(99999)` returns `0` (under threshold).
  - `getDiscountBySubtotal(150000)` returns `20000`.

### `Tính tổng đơn hàng (integration)`
- **Standard total calculations**:
  - Subtotal `50.000 ₫` -> Total `68.000 ₫` (`50000 + 18000 - 0`)
  - Subtotal `100.000 ₫` -> Total `98.000 ₫` (`100000 + 18000 - 20000`)
  - Subtotal `0 ₫` -> Total `0`

---

## 4. Specific View Helper Utilities

### `browseViewUtils.spec.js`
- **Yêu thích (Favorites)**: Verifies `toggleFavorite` pushes and pulls restaurant IDs in local storage.
- **Category Assets**: Verifies `getCategoryImageUrl` assigns default food cover drawings when exact URLs are absent.
- **Size Variations Pricing**: Verifies `getItemPriceBySize` performs standard multipliers when item objects lack absolute values.

### `cartViewUtils.spec.js` & `checkoutViewUtils.spec.js`
- **Payload transformation**: Verifies mapping structures correctly represent order payloads and address keys for backend endpoint payloads.

### `homeViewUtils.spec.js`
- **Carousel configurations**: Verifies banners slide count mappings.

### `profileViewUtils.spec.js`
- **Account updates**: Verifies input mappings for profile updating payloads.
