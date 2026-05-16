package com.example.server.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, name = "full_name")
	private String fullName;

	@Column(unique = true, length = 15)
	private String phone;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(nullable = false)
	private String role;

	@Column(name = "is_active")
	@Builder.Default
	private Boolean active = true;

	@Column(name = "is_deleted")
	@Builder.Default
	private Boolean deleted = false;

	@Column(name = "failed_login_attempts", nullable = false)
	@Builder.Default
	private Integer failedLoginAttempts = 0;

	@Column(name = "account_locked_until")
	private LocalDateTime accountLockedUntil;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Address> addresses = new ArrayList<>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Restaurant> restaurants = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<DeliveryAssignment> deliveryAssignment = new ArrayList<>();

	@OneToMany(mappedBy = "changedBy", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Notification> notifications = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OwnerRequest> ownerRequests = new ArrayList<>();

	@OneToOne(mappedBy = "shipper", cascade = CascadeType.ALL, orphanRemoval = true)
	private ShipperLocation shipperLocation;
}