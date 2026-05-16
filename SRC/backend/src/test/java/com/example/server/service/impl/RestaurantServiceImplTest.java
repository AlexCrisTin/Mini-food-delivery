package com.example.server.service.impl;

import com.example.server.dto.restaurant.*;
import com.example.server.entity.Restaurant;
import com.example.server.entity.RestaurantCategory;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.RestaurantMapper;
import com.example.server.repository.RestaurantCategoryRepository;
import com.example.server.repository.RestaurantRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantCategoryRepository categoryRepository;

    @Spy
    private RestaurantMapper restaurantMapper = Mappers.getMapper(RestaurantMapper.class);

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private User owner;
    private Restaurant restaurant;
    private Long restaurantId = 1L;
    private Long ownerId = 2L;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(ownerId);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Test Restaurant");
        restaurant.setOwner(owner);
        restaurant.setIsDeleted(false);
    }

    @Test
    void shouldSearchRestaurantsSuccessfully() {
        RestaurantSearchRequest request = new RestaurantSearchRequest();
        request.setPage(0);
        request.setSize(10);

        Page<Restaurant> page = new PageImpl<>(Collections.singletonList(restaurant));
        when(restaurantRepository.searchRestaurants(any(), any(), any(PageRequest.class))).thenReturn(page);

        var response = restaurantService.searchRestaurants(request);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        verify(restaurantRepository).searchRestaurants(any(), any(), any());
    }

    @Test
    void shouldGetRestaurantDetailSuccessfully() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        var response = restaurantService.getRestaurantDetail(restaurantId);

        assertNotNull(response);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNotFound() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.getRestaurantDetail(restaurantId));
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIsDeleted() {
        restaurant.setIsDeleted(true);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.getRestaurantDetail(restaurantId));
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("New Restaurant");
        request.setCategoryId(1L);

        RestaurantCategory category = new RestaurantCategory();
        category.setId(1L);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        var response = restaurantService.createRestaurant(ownerId, request);

        assertNotNull(response);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldUpdateRestaurantSuccessfully() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Name");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        var response = restaurantService.updateRestaurant(ownerId, restaurantId, request);

        assertNotNull(response);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingOtherOwnerRestaurant() {
        RestaurantRequest request = new RestaurantRequest();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(AppException.class, () -> restaurantService.updateRestaurant(999L, restaurantId, request));
    }

    @Test
    void shouldDeleteRestaurantSuccessfully() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        restaurantService.deleteRestaurant(ownerId, restaurantId);

        assertTrue(restaurant.getIsDeleted());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenDeletingOtherOwnerRestaurant() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(AppException.class, () -> restaurantService.deleteRestaurant(999L, restaurantId));
    }

    @Test
    void shouldGetAllCategories() {
        RestaurantCategory category = new RestaurantCategory();
        category.setId(1L);
        category.setName("Category");

        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(Collections.singletonList(category));

        var response = restaurantService.getAllCategories();

        assertEquals(1, response.size());
        assertEquals("Category", response.get(0).getName());
    }
}
