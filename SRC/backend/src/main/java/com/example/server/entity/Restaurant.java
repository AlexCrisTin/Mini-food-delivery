package com.example.server.entity;

import java.time.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// private Long owner_id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	// private Long categoryId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private RestaurantCategory category;

	@Column(name = "name", nullable = false)
	private String name;
	private String description;

	@Column(length = 15)
	private String phone;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(precision = 11, scale = 8)
	private BigDecimal longitude;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "opening_time")
	private LocalTime openingTime;
	@Column(name = "closing_time")
	private LocalTime closingTime;

	@Column(name = "is_open")
	private Boolean isOpen = true;

	@Column(name = "is_approved")
	private Boolean isApproved = false;

	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuItem> menuItems = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
}