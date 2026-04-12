package com.example.server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "restaurant_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(name = "icon_url")
	private String iconUrl;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Restaurant> restaurants = new ArrayList<>();
}