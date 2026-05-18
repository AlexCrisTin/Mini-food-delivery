# OrderControllerTest Detailed Tests

## `shouldCreateOrderSuccessfully`

**Scenario (Positive):** Create Order Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isCreated`
  - Verifies dependency interaction: `orderService.createOrder`

## `shouldGetOrderSummarySuccessfully`

**Scenario (Positive):** Get Order Summary Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
  - Verifies dependency interaction: `orderService.getOrderSummary`

## `shouldUpdateOrderStatusSuccessfully`

**Scenario (Positive):** Update Order Status Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
  - Verifies dependency interaction: `orderService.updateOrderStatus`
