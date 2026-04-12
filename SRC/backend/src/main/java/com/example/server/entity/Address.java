package com.example.server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @Column(name = "user_id", nullable = false)
	// private Long userId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String label;
	@Column(name = "address_line", nullable = false)
	private String addressLine;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(precision = 11, scale = 8)
	private BigDecimal longitude;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault = false;
}