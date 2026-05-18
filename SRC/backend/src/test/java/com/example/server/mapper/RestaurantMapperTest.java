package com.example.server.mapper;

import com.example.server.dto.restaurant.RestaurantCardResponse;
import com.example.server.dto.restaurant.RestaurantDetailResponse;
import com.example.server.dto.restaurant.RestaurantRequest;
import com.example.server.entity.Restaurant;
import com.example.server.entity.RestaurantCategory;
import com.example.server.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantMapperTest {

    private final RestaurantMapper mapper = Mappers.getMapper(RestaurantMapper.class);

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(mapper, "menuMapper", Mappers.getMapper(MenuMapper.class));
    }

    @Test
    void shouldMapToCardResponse() {
        RestaurantCategory category = new RestaurantCategory();
        category.setName("Fast Food");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Burger King");
        restaurant.setCategory(category);

        RestaurantCardResponse response = mapper.toCardResponse(restaurant);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Burger King", response.getName());
        assertEquals("Fast Food", response.getCategoryName());
    }

    @Test
    void shouldMapToCardResponseWithNulls() {
        assertNull(mapper.toCardResponse(null));
    }

    @Test
    void shouldMapToDetailResponse() {
        User owner = new User();
        owner.setId(10L);

        RestaurantCategory category = new RestaurantCategory();
        category.setId(20L);
        category.setName("Italian");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setOwner(owner);
        restaurant.setCategory(category);
        restaurant.setName("Pasta Place");

        RestaurantDetailResponse response = mapper.toDetailResponse(restaurant);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getOwnerId());
        assertEquals(20L, response.getCategoryId());
        assertEquals("Italian", response.getCategoryName());
        assertEquals("Pasta Place", response.getName());
    }

    @Test
    void shouldMapToDetailResponseWithNulls() {
        assertNull(mapper.toDetailResponse(null));
    }

    @Test
    void shouldMapToEntity() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("New Place");

        Restaurant restaurant = mapper.toEntity(request);

        assertNotNull(restaurant);
        assertNull(restaurant.getId());
        assertNull(restaurant.getOwner());
        assertEquals("New Place", restaurant.getName());
    }

    @Test
    void shouldMapToEntityWithNulls() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldUpdateEntity() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Old Name");

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Name");

        mapper.updateEntity(restaurant, request);

        assertEquals("Updated Name", restaurant.getName());
    }

    @Test
    void shouldNotUpdateEntityIfRequestIsNull() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Old Name");

        mapper.updateEntity(restaurant, null);

        assertEquals("Old Name", restaurant.getName());
    }
}
