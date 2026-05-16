package com.example.server.repository;

import com.example.server.BaseIntegrationTest;
import com.example.server.entity.Restaurant;
import com.example.server.entity.RestaurantCategory;
import com.example.server.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantRepositoryIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private RestaurantRepository restaurantRepository;

        @Autowired
        private RestaurantCategoryRepository categoryRepository;

        @Autowired
        private UserRepository userRepository;

        @Test
        void shouldSearchRestaurantsByKeywordsAndCategory() {
                // Arrange
                RestaurantCategory category = categoryRepository.save(RestaurantCategory.builder()
                                .name("Pizza")
                                .iconUrl("pizza.jpg")
                                .build());
                User owner = userRepository.save(User.builder().email("owner@test.com").password("pass").fullName("Owner One").role("ROLE_OWNER").build());

                Restaurant r1 = Restaurant.builder()
                        .name("Pizza Hut")
                        .category(category)
                        .owner(owner)
                        .isApproved(true)
                        .isDeleted(false)
                        .address("Address 1")
                        .build();
                Restaurant r2 = Restaurant.builder()
                        .name("Burger King")
                        .isApproved(true)
                        .isDeleted(false)
                        .address("Address 2")
                        .owner(owner)
                        .build();
                restaurantRepository.save(r1);
                restaurantRepository.save(r2);


                // Act
                Page<Restaurant> result = restaurantRepository.searchRestaurants("Pizza", category.getId(),
                                PageRequest.of(0, 10));

                // Assert
                assertEquals(1, result.getTotalElements());
                assertEquals("Pizza Hut", result.getContent().get(0).getName());
        }

        @Test
        void shouldFindRestaurantByIdWithEntityGraph() {
                // Arrange
                RestaurantCategory category = categoryRepository.save(RestaurantCategory.builder()
                                .name("Chinese")
                                .iconUrl("ff.jpg")
                                .build());
                User owner = userRepository.save(
                                User.builder().email("owner2@test.com").password("pass").fullName("Owner Two").role("ROLE_OWNER").build());
                Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                                .name("KFC")
                                .category(category)
                                .owner(owner)
                                .address("Address KFC")
                                .build());

                // Act
                Optional<Restaurant> found = restaurantRepository.findById(restaurant.getId());

                // Assert
                assertTrue(found.isPresent());
                assertNotNull(found.get().getCategory());
                assertNotNull(found.get().getOwner());
        }

        @Test
        void shouldUpdateStatusWhenRestaurantIsApproved() {
                // Arrange
                Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                                .name("New Resto")
                                .isApproved(false)
                                .address("New Address")
                                .owner(userRepository.save(User.builder().email("owner3@test.com").password("pass")
                                                .fullName("Owner Three").role("ROLE_OWNER").build()))
                                .build());

                // Act
                restaurantRepository.approveRestaurant(restaurant.getId());

                // Assert
                Restaurant updated = restaurantRepository.findById(restaurant.getId()).get();
                assertTrue(updated.getIsApproved());
        }
}
