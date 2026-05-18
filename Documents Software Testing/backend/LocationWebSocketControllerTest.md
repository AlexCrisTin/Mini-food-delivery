# LocationWebSocketControllerTest Detailed Tests

## `shouldHandleShipperLocationSuccessfully`

**Scenario (Positive):** Handle Shipper Location Successfully

- **Expected Outcomes:**
  - Verifies dependency interaction: `shipperLocationRepository.save`
  - Verifies dependency interaction: `messagingTemplate.convertAndSend`

## `shouldUpdateExistingShipperLocationSuccessfully`

**Scenario (Positive):** Update Existing Shipper Location Successfully

- **Expected Outcomes:**
  - Verifies dependency interaction: `shipperLocationRepository.save`
  - Verifies dependency interaction: `messagingTemplate.convertAndSend`

## `shouldIgnoreWhenPrincipalIsNull`

**Scenario (Negative):** Ignore When Principal Is Null
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Throws Exception / Returns Error (Implicit regex match failure, verify source for specific exception class).

## `shouldIgnoreWhenShipperIdMismatch`

**Scenario (Negative):** Ignore When Shipper Id Mismatch
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Throws Exception / Returns Error (Implicit regex match failure, verify source for specific exception class).
