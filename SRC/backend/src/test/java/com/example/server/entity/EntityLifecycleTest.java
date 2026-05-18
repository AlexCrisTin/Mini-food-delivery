package com.example.server.entity;

import com.example.server.enums.OwnerRequestStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EntityLifecycleTest {

    @Test
    void testUserLifecycle() {
        User user = new User();
        user.onCreate();
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        user.onUpdate();
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testRestaurantLifecycle() {
        Restaurant restaurant = new Restaurant();
        restaurant.onCreate();
        assertNotNull(restaurant.getCreatedAt());
        assertNotNull(restaurant.getUpdatedAt());

        restaurant.onUpdate();
        assertNotNull(restaurant.getUpdatedAt());
    }

    @Test
    void testOrderLifecycle() {
        Order order = new Order();
        order.onCreate();
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());

        order.onUpdate();
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testMenuItemLifecycle() {
        MenuItem item = new MenuItem();
        item.onCreate();
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());

        item.onUpdate();
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void testOwnerRequestLifecycle() {
        OwnerRequest request = new OwnerRequest();
        request.onCreate();
        assertNotNull(request.getCreatedAt());
        assertEquals(OwnerRequestStatus.PENDING, request.getStatus());

        request.onUpdate();
        assertNotNull(request.getUpdatedAt());
    }

    @Test
    void testDeliveryAssignmentLifecycle() {
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.onCreate();
        assertNotNull(assignment.getCreatedAt());
    }

    @Test
    void testNotificationLifecycle() {
        Notification notification = new Notification();
        notification.onCreate();
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void testOrderStatusHistoryLifecycle() {
        OrderStatusHistory history = new OrderStatusHistory();
        history.onCreate();
        assertNotNull(history.getCreatedAt());
    }

    @Test
    void testShipperLocationLifecycle() {
        ShipperLocation location = new ShipperLocation();
        location.onCreate();
        assertNotNull(location.getUpdatedAt());

        location.onUpdate();
        assertNotNull(location.getUpdatedAt());
    }

    @Test
    void testShipperRequestLifecycle() {
        ShipperRequest request = new ShipperRequest();
        request.onCreate();
        assertNotNull(request.getCreatedAt());

        request.onUpdate();
        assertNotNull(request.getUpdatedAt());
    }

    @Test
    void testRestaurantCategoryLifecycle() {
        RestaurantCategory category = new RestaurantCategory();
        category.onCreate();
        assertNotNull(category.getCreatedAt());

        category.onUpdate();
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testUserAndRestaurantBuilderDefaults() {
        Restaurant restaurant = Restaurant.builder().build();
        assertTrue(restaurant.getIsOpen());
        assertFalse(restaurant.getIsApproved());
        assertFalse(restaurant.getIsDeleted());
        assertNotNull(restaurant.getMenuItems());
        assertNotNull(restaurant.getMenuCategories());
        assertNotNull(restaurant.getOrders());

        User user = User.builder().build();
        assertTrue(user.getActive());
        assertFalse(user.getDeleted());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNotNull(user.getAddresses());
        assertNotNull(user.getRestaurants());
        assertNotNull(user.getOrders());
        assertNotNull(user.getDeliveryAssignment());
        assertNotNull(user.getOrderStatusHistories());
        assertNotNull(user.getNotifications());
        assertNotNull(user.getOwnerRequests());
    }

    @Test
    void testOrderAndMenuBuilderDefaults() {
        MenuCategory category = MenuCategory.builder().build();
        assertEquals(0, category.getSortOrder());
        assertFalse(category.getIsDeleted());

        MenuItem item = MenuItem.builder().build();
        assertTrue(item.getIsAvailable());
        assertFalse(item.getIsDeleted());

        Order order = Order.builder().build();
        assertEquals(BigDecimal.ZERO, order.getDeliveryFee());
        assertEquals("COD", order.getPaymentMethod());
        assertEquals("PENDING", order.getStatus());
        assertFalse(order.getIsPaid());
        assertNotNull(order.getOrderItems());
        assertNotNull(order.getStatusHistories());
    }

    @Test
    void testOtherBuilderDefaults() {
        Address address = Address.builder().build();
        assertFalse(address.getIsDefault());

        DeliveryAssignment assignment = DeliveryAssignment.builder().build();
        assertEquals("UNASSIGNED", assignment.getStatus());

        Notification notification = Notification.builder().build();
        assertFalse(notification.getIsRead());

        ShipperLocation loc = ShipperLocation.builder().build();
        assertFalse(loc.getIsOnline());
    }
}
