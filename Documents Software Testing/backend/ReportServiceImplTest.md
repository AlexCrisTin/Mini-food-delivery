# ReportServiceImplTest Detailed Tests

## `shouldAggregateAdminReportDataCorrectly`

**Scenario (Positive):** Aggregate Admin Report Data Correctly

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - State/Persistence Assertion: `new BigDecimal("1000.00"), result.getTotalRevenue()`
  - State/Persistence Assertion: `50L, result.getDeliveredOrderCount()`
  - State/Persistence Assertion: `100L, result.getActiveUserCount()`
  - State/Persistence Assertion: `10L, result.getApprovedRestaurantCount()`
  - State/Persistence Assertion: `start, result.getStartDate()`
  - State/Persistence Assertion: `end, result.getEndDate()`

## `shouldReturnZeroRevenueWhenNoOrdersFound`

**Scenario (Positive):** Return Zero Revenue When No Orders Found

- **Expected Outcomes:**
  - State/Persistence Assertion: `BigDecimal.ZERO, result.getTotalRevenue()`

## `shouldGetRestaurantRevenueSuccessfully`

**Scenario (Positive):** Get Restaurant Revenue Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`
  - State/Persistence Assertion: `"Restaurant A", result.get(0).getRestaurantName()`
  - State/Persistence Assertion: `new BigDecimal("500.00"), result.get(0).getTotalRevenue()`

## `shouldGenerateRevenueCsvSuccessfully`

**Scenario (Positive):** Generate Revenue Csv Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `csv.contains("Restaurant ID,Restaurant Name,Order Count,Total Revenue")`
  - State/Persistence Assertion: `csv.contains("1,\"Restaurant \"\"A\"\"\",5,500.00")`
