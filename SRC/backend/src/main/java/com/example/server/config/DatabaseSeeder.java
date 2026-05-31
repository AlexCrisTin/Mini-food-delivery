package com.example.server.config;

import com.example.server.entity.*;
import com.example.server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping seeder.");
            return;
        }

        log.info("🌱 Seeding demonstration database...");

        // 1. Seed Users
        String defaultPassword = passwordEncoder.encode("password123");

        User admin = User.builder()
                .email("admin@fooddelivery.com")
                .password(defaultPassword)
                .fullName("System Admin")
                .phone("0901111111")
                .role("ADMIN")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(admin);

        User owner1 = User.builder()
                .email("owner1@restaurant.com")
                .password(defaultPassword)
                .fullName("John Owner")
                .phone("0902222222")
                .role("OWNER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(owner1);

        User owner2 = User.builder()
                .email("owner2@restaurant.com")
                .password(defaultPassword)
                .fullName("Sarah Bistro")
                .phone("0903333333")
                .role("OWNER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(owner2);

        User shipper1 = User.builder()
                .email("shipper1@shipper.com")
                .password(defaultPassword)
                .fullName("David Swift")
                .phone("0904444444")
                .role("SHIPPER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(shipper1);

        User shipper2 = User.builder()
                .email("shipper2@shipper.com")
                .password(defaultPassword)
                .fullName("Emma Rider")
                .phone("0905555555")
                .role("SHIPPER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(shipper2);

        User customer1 = User.builder()
                .email("customer1@customer.com")
                .password(defaultPassword)
                .fullName("Alice Smith")
                .phone("0906666666")
                .role("CUSTOMER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(customer1);

        User customer2 = User.builder()
                .email("customer2@customer.com")
                .password(defaultPassword)
                .fullName("Bob Johnson")
                .phone("0907777777")
                .role("CUSTOMER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(customer2);

        User customer3 = User.builder()
                .email("customer3@customer.com")
                .password(defaultPassword)
                .fullName("Charlie Brown")
                .phone("0908888888")
                .role("CUSTOMER")
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(customer3);

        log.info("✔ Seeded 8 users (Admin, Owners, Shippers, Customers)");

        // 2. Seed Addresses for Customers
        Address addr1 = Address.builder()
                .user(customer1)
                .label("Home")
                .addressLine("120 Nguyen Hue St, District 1, HCMC")
                .latitude(new BigDecimal("10.77250000"))
                .longitude(new BigDecimal("106.70180000"))
                .isDefault(true)
                .build();
        addressRepository.save(addr1);

        Address addr2 = Address.builder()
                .user(customer2)
                .label("Work")
                .addressLine("88 Le Loi St, District 1, HCMC")
                .latitude(new BigDecimal("10.77500000"))
                .longitude(new BigDecimal("106.69800000"))
                .isDefault(true)
                .build();
        addressRepository.save(addr2);

        Address addr3 = Address.builder()
                .user(customer3)
                .label("Apartment")
                .addressLine("34 Pasteur St, District 1, HCMC")
                .latitude(new BigDecimal("10.77020000"))
                .longitude(new BigDecimal("106.70010000"))
                .isDefault(true)
                .build();
        addressRepository.save(addr3);

        log.info("✔ Seeded default addresses for customers");

        // 3. Retrieve categories (Flyway seeds: Rice, Fast Food, Sea Food, Dry Dish, Soup Dish, Drink, Dessert)
        RestaurantCategory catFastFood = restaurantCategoryRepository.findByName("Fast Food").orElse(null);
        RestaurantCategory catSeaFood = restaurantCategoryRepository.findByName("Sea Food").orElse(null);
        RestaurantCategory catDrink = restaurantCategoryRepository.findByName("Drink").orElse(null);

        // Fallbacks if categories don't exist yet for some reason
        if (catFastFood == null) {
            catFastFood = restaurantCategoryRepository.save(RestaurantCategory.builder().name("Fast Food").iconUrl("https://cdn-icons-png.flaticon.com/512/737/737967.png").build());
        }
        if (catSeaFood == null) {
            catSeaFood = restaurantCategoryRepository.save(RestaurantCategory.builder().name("Sea Food").iconUrl("https://cdn-icons-png.flaticon.com/512/2927/2927347.png").build());
        }
        if (catDrink == null) {
            catDrink = restaurantCategoryRepository.save(RestaurantCategory.builder().name("Drink").iconUrl("https://cdn-icons-png.flaticon.com/512/3121/3121784.png").build());
        }

        // 4. Seed Restaurants
        Restaurant r1 = Restaurant.builder()
                .owner(owner1)
                .category(catFastFood)
                .name("Gourmet Burger Bistro")
                .description("Juicy, premium smash burgers and artisan truffle fries.")
                .phone("0281234567")
                .address("220 Nguyen Hue St, District 1, HCMC")
                .latitude(new BigDecimal("10.77350000"))
                .longitude(new BigDecimal("106.70250000"))
                .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80")
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(22, 0))
                .isOpen(true)
                .isApproved(true)
                .isDeleted(false)
                .build();
        restaurantRepository.save(r1);

        Restaurant r2 = Restaurant.builder()
                .owner(owner1)
                .category(catSeaFood)
                .name("Sushi Master Zen")
                .description("Authentic Japanese sushi, sashimi, and custom hand rolls.")
                .phone("0281234568")
                .address("88 Le Loi St, District 1, HCMC")
                .latitude(new BigDecimal("10.77600000"))
                .longitude(new BigDecimal("106.69900000"))
                .imageUrl("https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=600&q=80")
                .openingTime(LocalTime.of(10, 0))
                .closingTime(LocalTime.of(21, 30))
                .isOpen(true)
                .isApproved(true)
                .isDeleted(false)
                .build();
        restaurantRepository.save(r2);

        Restaurant r3 = Restaurant.builder()
                .owner(owner2)
                .category(catDrink)
                .name("Bubble Tea Oasis")
                .description("Creamy brown sugar milk teas, fresh fruit teas, and matcha options.")
                .phone("0281234569")
                .address("15 Dong Khoi St, District 1, HCMC")
                .latitude(new BigDecimal("10.77150000"))
                .longitude(new BigDecimal("106.70080000"))
                .imageUrl("https://images.unsplash.com/photo-1541658016709-82535e94bc69?auto=format&fit=crop&w=600&q=80")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(22, 30))
                .isOpen(true)
                .isApproved(true)
                .isDeleted(false)
                .build();
        restaurantRepository.save(r3);

        Restaurant r4 = Restaurant.builder()
                .owner(owner2)
                .category(catFastFood)
                .name("Pizzeria Bella Italia")
                .description("Wood-fired thin crust pizzas crafted with imported Italian flour.")
                .phone("0281234570")
                .address("34 Pasteur St, District 1, HCMC")
                .latitude(new BigDecimal("10.77450000"))
                .longitude(new BigDecimal("106.70120000"))
                .imageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=600&q=80")
                .openingTime(LocalTime.of(11, 0))
                .closingTime(LocalTime.of(23, 0))
                .isOpen(true)
                .isApproved(true)
                .isDeleted(false)
                .build();
        restaurantRepository.save(r4);

        log.info("✔ Seeded 4 approved restaurants");

        // 5. Seed Menu Categories & Menu Items
        // --- Restaurant 1: Gourmet Burger Bistro ---
        MenuCategory r1CatBurgers = menuCategoryRepository.save(MenuCategory.builder().restaurant(r1).name("Burgers").sortOrder(1).isDeleted(false).build());
        MenuCategory r1CatSides = menuCategoryRepository.save(MenuCategory.builder().restaurant(r1).name("Sides").sortOrder(2).isDeleted(false).build());
        MenuCategory r1CatDrinks = menuCategoryRepository.save(MenuCategory.builder().restaurant(r1).name("Drinks").sortOrder(3).isDeleted(false).build());

        MenuItem itemClassicBurger = menuItemRepository.save(MenuItem.builder()
                .restaurant(r1)
                .category(r1CatBurgers)
                .name("Classic Cheeseburger")
                .description("Premium beef patty, cheddar, lettuce, tomato, and house burger sauce.")
                .price(new BigDecimal("8.99"))
                .imageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemBaconBurger = menuItemRepository.save(MenuItem.builder()
                .restaurant(r1)
                .category(r1CatBurgers)
                .name("Bacon Double Deluxe")
                .description("Two beef patties, crispy smoked bacon, double cheddar, and BBQ glaze.")
                .price(new BigDecimal("11.99"))
                .imageUrl("https://images.unsplash.com/photo-1553979459-d2229ba7433b?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemFries = menuItemRepository.save(MenuItem.builder()
                .restaurant(r1)
                .category(r1CatSides)
                .name("Crispy Truffle Fries")
                .description("Golden french fries tossed in pure white truffle oil and fresh parmesan.")
                .price(new BigDecimal("4.50"))
                .imageUrl("https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemShake = menuItemRepository.save(MenuItem.builder()
                .restaurant(r1)
                .category(r1CatDrinks)
                .name("Vanilla Milkshake")
                .description("Creamy house-churned vanilla bean ice cream topped with whipped cream.")
                .price(new BigDecimal("3.99"))
                .imageUrl("https://images.unsplash.com/photo-1572490122747-3968b75cc699?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        // --- Restaurant 2: Sushi Master Zen ---
        MenuCategory r2CatRolls = menuCategoryRepository.save(MenuCategory.builder().restaurant(r2).name("Sushi Rolls").sortOrder(1).isDeleted(false).build());
        MenuCategory r2CatSashimi = menuCategoryRepository.save(MenuCategory.builder().restaurant(r2).name("Sashimi").sortOrder(2).isDeleted(false).build());

        MenuItem itemSashimi = menuItemRepository.save(MenuItem.builder()
                .restaurant(r2)
                .category(r2CatSashimi)
                .name("Salmon Sashimi")
                .description("5 pieces of thinly sliced, fresh Norwegian Salmon served with wasabi.")
                .price(new BigDecimal("12.99"))
                .imageUrl("https://images.unsplash.com/photo-1534482421-64566f976cfa?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemCalRoll = menuItemRepository.save(MenuItem.builder()
                .restaurant(r2)
                .category(r2CatRolls)
                .name("California Roll")
                .description("Crab meat, cucumber, and creamy avocado rolled inside sesame rice.")
                .price(new BigDecimal("7.99"))
                .imageUrl("https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemDragRoll = menuItemRepository.save(MenuItem.builder()
                .restaurant(r2)
                .category(r2CatRolls)
                .name("Dragon Roll Deluxe")
                .description("Eel and cucumber inside, layered with avocado and unagi sauce outside.")
                .price(new BigDecimal("14.99"))
                .imageUrl("https://images.unsplash.com/photo-1611143669185-af224c5e3252?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        // --- Restaurant 3: Bubble Tea Oasis ---
        MenuCategory r3CatMilkV = menuCategoryRepository.save(MenuCategory.builder().restaurant(r3).name("Milk Teas").sortOrder(1).isDeleted(false).build());
        MenuCategory r3CatFruit = menuCategoryRepository.save(MenuCategory.builder().restaurant(r3).name("Fruit Teas").sortOrder(2).isDeleted(false).build());

        MenuItem itemBrownSugar = menuItemRepository.save(MenuItem.builder()
                .restaurant(r3)
                .category(r3CatMilkV)
                .name("Brown Sugar Milk Tea")
                .description("Signature black milk tea layered with caramelized brown sugar tapioca pearls.")
                .price(new BigDecimal("5.50"))
                .imageUrl("https://images.unsplash.com/photo-1541658016709-82535e94bc69?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemMatcha = menuItemRepository.save(MenuItem.builder()
                .restaurant(r3)
                .category(r3CatMilkV)
                .name("Matcha Latte with Red Bean")
                .description("Organic Japanese Uji Matcha latte layered with sweet azuki red beans.")
                .price(new BigDecimal("5.90"))
                .imageUrl("https://images.unsplash.com/photo-1536256263959-770b48d82b0a?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemMangoTea = menuItemRepository.save(MenuItem.builder()
                .restaurant(r3)
                .category(r3CatFruit)
                .name("Mango Passion Fruit Tea")
                .description("Refreshing jasmine green tea infused with real mango and passion fruit pulp.")
                .price(new BigDecimal("4.90"))
                .imageUrl("https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        // --- Restaurant 4: Pizzeria Bella Italia ---
        MenuCategory r4CatPizzas = menuCategoryRepository.save(MenuCategory.builder().restaurant(r4).name("Pizzas").sortOrder(1).isDeleted(false).build());
        MenuCategory r4CatStarters = menuCategoryRepository.save(MenuCategory.builder().restaurant(r4).name("Starters").sortOrder(2).isDeleted(false).build());

        MenuItem itemPizzaMarg = menuItemRepository.save(MenuItem.builder()
                .restaurant(r4)
                .category(r4CatPizzas)
                .name("Margherita Pizza")
                .description("Fresh tomato sauce, buffalo mozzarella, garden basil, and extra virgin olive oil.")
                .price(new BigDecimal("10.99"))
                .imageUrl("https://images.unsplash.com/photo-1604382355076-af4b0eb60143?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemPizzaPep = menuItemRepository.save(MenuItem.builder()
                .restaurant(r4)
                .category(r4CatPizzas)
                .name("Pepperoni Feast Pizza")
                .description("Robust tomato sauce, double mozzarella, and thick-cut spicy Italian pepperoni.")
                .price(new BigDecimal("13.99"))
                .imageUrl("https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        MenuItem itemGarlicBread = menuItemRepository.save(MenuItem.builder()
                .restaurant(r4)
                .category(r4CatStarters)
                .name("Garlic Breadsticks")
                .description("Freshly baked breadsticks brushed with wild garlic butter and parsley.")
                .price(new BigDecimal("4.99"))
                .imageUrl("https://images.unsplash.com/photo-1544982503-9f984c14501a?auto=format&fit=crop&w=300&q=80")
                .isAvailable(true)
                .isDeleted(false)
                .build());

        log.info("✔ Seeded restaurant menus and 13 culinary items");

        // 6. Seed Historical and Active Orders
        // --- Order 1 (DELIVERED) ---
        // Customer 1 bought from Gourmet Burger Bistro
        Order o1 = Order.builder()
                .user(customer1)
                .restaurant(r1)
                .deliveryAddress(addr1.getAddressLine())
                .deliveryLat(addr1.getLatitude())
                .deliveryLng(addr1.getLongitude())
                .subtotal(new BigDecimal("22.48")) // 2 Cheeseburger ($17.98) + 1 Truffle Fries ($4.50)
                .deliveryFee(new BigDecimal("2.50"))
                .totalAmount(new BigDecimal("24.98"))
                .paymentMethod("COD")
                .status("DELIVERED")
                .isPaid(true)
                .note("Please leave at front gate.")
                .build();
        orderRepository.save(o1);

        orderItemRepository.save(OrderItem.builder()
                .order(o1)
                .menuItem(itemClassicBurger)
                .itemName(itemClassicBurger.getName())
                .itemPrice(itemClassicBurger.getPrice())
                .quantity(2)
                .subtotal(new BigDecimal("17.98"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(o1)
                .menuItem(itemFries)
                .itemName(itemFries.getName())
                .itemPrice(itemFries.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("4.50"))
                .build());

        // Create Order Status Histories
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o1).status("PENDING").changedBy(customer1).note("Order submitted").build());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o1).status("CONFIRMED").changedBy(owner1).note("Order accepted").build());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o1).status("DELIVERED").changedBy(shipper1).note("Delivered successfully").build());

        // Create Delivery Assignment
        deliveryAssignmentRepository.save(DeliveryAssignment.builder()
                .order(o1)
                .shipper(shipper1)
                .status("DELIVERED")
                .pickedUpAt(LocalDateTime.now().minusDays(1).plusMinutes(15))
                .deliveredAt(LocalDateTime.now().minusDays(1).plusMinutes(35))
                .build());

        // --- Order 2 (DELIVERED) ---
        // Customer 2 bought from Pizzeria Bella Italia
        Order o2 = Order.builder()
                .user(customer2)
                .restaurant(r4)
                .deliveryAddress(addr2.getAddressLine())
                .deliveryLat(addr2.getLatitude())
                .deliveryLng(addr2.getLongitude())
                .subtotal(new BigDecimal("18.98")) // 1 Pepperoni Pizza ($13.99) + 1 Garlic Breadsticks ($4.99)
                .deliveryFee(new BigDecimal("3.00"))
                .totalAmount(new BigDecimal("21.98"))
                .paymentMethod("COD")
                .status("DELIVERED")
                .isPaid(true)
                .note("Ring doorbell.")
                .build();
        orderRepository.save(o2);

        orderItemRepository.save(OrderItem.builder()
                .order(o2)
                .menuItem(itemPizzaPep)
                .itemName(itemPizzaPep.getName())
                .itemPrice(itemPizzaPep.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("13.99"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(o2)
                .menuItem(itemGarlicBread)
                .itemName(itemGarlicBread.getName())
                .itemPrice(itemGarlicBread.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("4.99"))
                .build());

        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o2).status("PENDING").changedBy(customer2).note("Order submitted").build());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o2).status("DELIVERED").changedBy(shipper2).note("Delivered successfully").build());

        deliveryAssignmentRepository.save(DeliveryAssignment.builder()
                .order(o2)
                .shipper(shipper2)
                .status("DELIVERED")
                .pickedUpAt(LocalDateTime.now().minusDays(2).plusMinutes(10))
                .deliveredAt(LocalDateTime.now().minusDays(2).plusMinutes(30))
                .build());

        // --- Order 3 (ACTIVE: READY / SHIPPING) ---
        // Customer 1 bought from Sushi Master Zen
        Order o3 = Order.builder()
                .user(customer1)
                .restaurant(r2)
                .deliveryAddress(addr1.getAddressLine())
                .deliveryLat(addr1.getLatitude())
                .deliveryLng(addr1.getLongitude())
                .subtotal(new BigDecimal("20.98")) // 1 California Roll ($7.99) + 1 Salmon Sashimi ($12.99)
                .deliveryFee(new BigDecimal("2.50"))
                .totalAmount(new BigDecimal("23.48"))
                .paymentMethod("COD")
                .status("SHIPPING")
                .isPaid(false)
                .note("Careful with the raw fish, keep it cool please!")
                .build();
        orderRepository.save(o3);

        orderItemRepository.save(OrderItem.builder()
                .order(o3)
                .menuItem(itemCalRoll)
                .itemName(itemCalRoll.getName())
                .itemPrice(itemCalRoll.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("7.99"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(o3)
                .menuItem(itemSashimi)
                .itemName(itemSashimi.getName())
                .itemPrice(itemSashimi.getPrice())
                .quantity(1)
                .subtotal(new BigDecimal("12.99"))
                .build());

        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o3).status("PENDING").changedBy(customer1).note("Order submitted").build());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o3).status("CONFIRMED").changedBy(owner1).note("Chef is starting preparation").build());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o3).status("SHIPPING").changedBy(shipper1).note("Shipper is on the way").build());

        deliveryAssignmentRepository.save(DeliveryAssignment.builder()
                .order(o3)
                .shipper(shipper1)
                .status("PICKED_UP")
                .pickedUpAt(LocalDateTime.now().minusMinutes(5))
                .build());

        // --- Order 4 (PENDING) ---
        // Customer 3 bought from Bubble Tea Oasis
        Order o4 = Order.builder()
                .user(customer3)
                .restaurant(r3)
                .deliveryAddress(addr3.getAddressLine())
                .deliveryLat(addr3.getLatitude())
                .deliveryLng(addr3.getLongitude())
                .subtotal(new BigDecimal("11.00")) // 2 brown sugar milk teas
                .deliveryFee(new BigDecimal("1.50"))
                .totalAmount(new BigDecimal("12.50"))
                .paymentMethod("COD")
                .status("PENDING")
                .isPaid(false)
                .note("Less sweet, less ice please!")
                .build();
        orderRepository.save(o4);

        orderItemRepository.save(OrderItem.builder()
                .order(o4)
                .menuItem(itemBrownSugar)
                .itemName(itemBrownSugar.getName())
                .itemPrice(itemBrownSugar.getPrice())
                .quantity(2)
                .subtotal(new BigDecimal("11.00"))
                .build());

        orderStatusHistoryRepository.save(OrderStatusHistory.builder().order(o4).status("PENDING").changedBy(customer3).note("Order submitted").build());

        deliveryAssignmentRepository.save(DeliveryAssignment.builder()
                .order(o4)
                .shipper(null)
                .status("UNASSIGNED")
                .build());

        log.info("✔ Seeded completed orders and active tracking logs");
        log.info("🌱 Database seeding complete!");
    }
}
