package com.example.server.service.impl;

import com.example.server.dto.map.GeocodingResponse;
import com.example.server.dto.map.RoutingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MapServiceImplTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private MapServiceImpl mapService;

    @BeforeEach
    void setUp() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), (Object[]) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(), any(), any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shouldSearchAddressSuccessfully() {
        GeocodingResponse geocodingResponse = new GeocodingResponse();
        geocodingResponse.setDisplayName("Hanoi, Vietnam");
        List<GeocodingResponse> responses = Collections.singletonList(geocodingResponse);

        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(responses);

        List<GeocodingResponse> result = mapService.searchAddress("Hanoi");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hanoi, Vietnam", result.get(0).getDisplayName());
    }

    @Test
    void shouldReturnEmptyListWhenGeocodingFails() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenThrow(new RuntimeException("API Error"));

        List<GeocodingResponse> result = mapService.searchAddress("error");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetRouteSuccessfully() {
        RoutingResponse routingResponse = new RoutingResponse();
        RoutingResponse.Route route = new RoutingResponse.Route();
        route.setDistance(2500.0);
        routingResponse.setRoutes(Collections.singletonList(route));

        when(responseSpec.body(RoutingResponse.class)).thenReturn(routingResponse);

        RoutingResponse result = mapService.getRoute(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);

        assertNotNull(result);
        assertEquals(1, result.getRoutes().size());
        assertEquals(2500.0, result.getRoutes().get(0).getDistance());
    }

    @Test
    void shouldReturnNullWhenRoutingFails() {
        when(responseSpec.body(RoutingResponse.class)).thenThrow(new RuntimeException("OSRM Error"));

        RoutingResponse result = mapService.getRoute(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE);

        assertNull(result);
    }
}
