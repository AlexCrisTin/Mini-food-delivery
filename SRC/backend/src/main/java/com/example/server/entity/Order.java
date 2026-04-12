package com.example.server.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Column(name = "delivery_address", nullable = false)
	private String deliveryAddress;

	@Column(name = "delivery_lat", nullable = false, precision = 12, scale = 2)
	private BigDecimal deliveryLat;

	@Column(name = "delivery_lng", nullable = false, precision = 12, scale = 2)
	private BigDecimal deliveryLng;

	@Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "delivery_fee", nullable = false, precision = 12, scale = 2)
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod = "COD";

	@Column(name = "status", nullable = false)
	private String status = "PENDING";

	@Column(name = "note")
	private String note;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
