package com.example.server.mapper;

import com.example.server.dto.restaurant.MenuCategoryResponse;
import com.example.server.dto.restaurant.MenuItemRequest;
import com.example.server.dto.restaurant.MenuItemResponse;
import com.example.server.entity.MenuCategory;
import com.example.server.entity.MenuItem;
import com.example.server.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MenuMapperTest {

    private final MenuMapper mapper = Mappers.getMapper(MenuMapper.class);

    @Test
    void shouldMapToItemResponse() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(10L);

        MenuCategory category = new MenuCategory();
        category.setId(20L);
        category.setName("Drinks");

        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setName("Coke");
        item.setPrice(new BigDecimal("2.50"));

        MenuItemResponse response = mapper.toItemResponse(item);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getRestaurantId());
        assertEquals(20L, response.getCategoryId());
        assertEquals("Drinks", response.getCategoryName());
        assertEquals("Coke", response.getName());
        assertEquals(new BigDecimal("2.50"), response.getPrice());
    }

    @Test
    void shouldMapToItemResponseWithNulls() {
        assertNull(mapper.toItemResponse(null));
        MenuItem item = new MenuItem();
        assertNotNull(mapper.toItemResponse(item)); // fields should be null safely
    }

    @Test
    void shouldMapToCategoryResponse() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(10L);

        MenuCategory category = new MenuCategory();
        category.setId(20L);
        category.setRestaurant(restaurant);
        category.setName("Drinks");

        MenuCategoryResponse response = mapper.toCategoryResponse(category);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals(10L, response.getRestaurantId());
        assertEquals("Drinks", response.getName());
    }

    @Test
    void shouldMapToCategoryResponseWithNulls() {
        assertNull(mapper.toCategoryResponse(null));
    }

    @Test
    void shouldMapToEntity() {
        MenuItemRequest request = new MenuItemRequest();
        request.setName("Pepsi");
        request.setPrice(new BigDecimal("2.00"));

        MenuItem item = mapper.toEntity(request);

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getRestaurant());
        assertEquals("Pepsi", item.getName());
    }

    @Test
    void shouldMapToEntityWithNulls() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldUpdateEntity() {
        MenuItem item = new MenuItem();
        item.setName("Old Name");

        MenuItemRequest request = new MenuItemRequest();
        request.setName("New Name");

        mapper.updateEntity(item, request);

        assertEquals("New Name", item.getName());
    }

    @Test
    void shouldNotUpdateEntityIfRequestIsNull() {
        MenuItem item = new MenuItem();
        item.setName("Old Name");

        mapper.updateEntity(item, null);

        assertEquals("Old Name", item.getName());
    }
}
