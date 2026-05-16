package com.example.server.service.impl;

import com.example.server.dto.restaurant.*;
import com.example.server.entity.MenuCategory;
import com.example.server.entity.MenuItem;
import com.example.server.entity.Restaurant;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.MenuMapper;
import com.example.server.repository.MenuCategoryRepository;
import com.example.server.repository.MenuItemRepository;
import com.example.server.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private MenuCategoryRepository menuCategoryRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Spy
    private MenuMapper menuMapper = Mappers.getMapper(MenuMapper.class);

    @InjectMocks
    private MenuServiceImpl menuService;

    private User owner;
    private Restaurant restaurant;
    private MenuCategory category;
    private MenuItem menuItem;
    private Long ownerId = 1L;
    private Long restaurantId = 2L;
    private Long categoryId = 3L;
    private Long itemId = 4L;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(ownerId);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwner(owner);

        category = new MenuCategory();
        category.setId(categoryId);
        category.setRestaurant(restaurant);
        category.setIsDeleted(false);

        menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setRestaurant(restaurant);
        menuItem.setCategory(category);
        menuItem.setIsDeleted(false);
    }

    @Test
    void shouldGetMenuCategoriesSuccessfully() {
        when(menuCategoryRepository.findByRestaurantIdOrderBySortOrderAsc(restaurantId))
                .thenReturn(Collections.singletonList(category));

        var response = menuService.getMenuCategories(restaurantId);

        assertEquals(1, response.size());
    }

    @Test
    void shouldAddMenuCategorySuccessfully() {
        MenuCategoryRequest request = new MenuCategoryRequest();
        request.setName("New Category");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.save(any(MenuCategory.class))).thenReturn(category);

        var response = menuService.addMenuCategory(ownerId, restaurantId, request);

        assertNotNull(response);
        verify(menuCategoryRepository).save(any(MenuCategory.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingCategoryToOtherOwnerRestaurant() {
        MenuCategoryRequest request = new MenuCategoryRequest();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(AppException.class, () -> menuService.addMenuCategory(999L, restaurantId, request));
    }

    @Test
    void shouldAddMenuItemSuccessfully() {
        MenuItemRequest request = new MenuItemRequest();
        request.setName("New Item");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        var response = menuService.addMenuItem(ownerId, restaurantId, categoryId, request);

        assertNotNull(response);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() {
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        menuService.deleteMenuItem(ownerId, itemId);

        assertTrue(menuItem.getIsDeleted());
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void shouldGetMenuItemSuccessfully() {
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));

        var response = menuService.getMenuItem(itemId);

        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenMenuItemIsDeleted() {
        menuItem.setIsDeleted(true);
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));

        assertThrows(ResourceNotFoundException.class, () -> menuService.getMenuItem(itemId));
    }
}
