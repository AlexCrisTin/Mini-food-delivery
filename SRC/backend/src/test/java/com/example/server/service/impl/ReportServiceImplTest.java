package com.example.server.service.impl;

import com.example.server.dto.report.AdminReportSummaryResponse;
import com.example.server.dto.report.RestaurantRevenueResponse;
import com.example.server.repository.OrderRepository;
import com.example.server.repository.RestaurantRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void shouldAggregateAdminReportDataCorrectly() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);

        when(orderRepository.sumTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(orderRepository.countDeliveredOrders(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(50L);
        when(userRepository.countByActiveTrue()).thenReturn(100L);
        when(restaurantRepository.countByIsApprovedTrue()).thenReturn(10L);

        AdminReportSummaryResponse result = reportService.getAdminReport(start, end);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getTotalRevenue());
        assertEquals(50L, result.getDeliveredOrderCount());
        assertEquals(100L, result.getActiveUserCount());
        assertEquals(10L, result.getApprovedRestaurantCount());
        assertEquals(start, result.getStartDate());
        assertEquals(end, result.getEndDate());
    }

    @Test
    void shouldReturnZeroRevenueWhenNoOrdersFound() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        when(orderRepository.sumTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);
        when(orderRepository.countDeliveredOrders(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(userRepository.countByActiveTrue()).thenReturn(0L);
        when(restaurantRepository.countByIsApprovedTrue()).thenReturn(0L);

        AdminReportSummaryResponse result = reportService.getAdminReport(start, end);

        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
    }

    @Test
    void shouldGetRestaurantRevenueSuccessfully() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        Object[] row = {1L, "Restaurant A", 5L, new BigDecimal("500.00")};
        when(orderRepository.findRestaurantRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(row));

        List<RestaurantRevenueResponse> result = reportService.getRestaurantRevenue(start, end);

        assertEquals(1, result.size());
        assertEquals("Restaurant A", result.get(0).getRestaurantName());
        assertEquals(new BigDecimal("500.00"), result.get(0).getTotalRevenue());
    }

    @Test
    void shouldGenerateRevenueCsvSuccessfully() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();
        Object[] row = {1L, "Restaurant \"A\"", 5L, new BigDecimal("500.00")};
        when(orderRepository.findRestaurantRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(row));

        byte[] csvBytes = reportService.generateRevenueCsv(start, end);
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("Restaurant ID,Restaurant Name,Order Count,Total Revenue"));
        assertTrue(csv.contains("1,\"Restaurant \"\"A\"\"\",5,500.00"));
    }
}
