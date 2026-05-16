package com.example.server.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import com.example.server.entity.Restaurant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
        @EntityGraph(attributePaths = { "category" })
        @Query("SELECT r FROM Restaurant r WHERE " +
                        "(:categoryId IS NULL OR r.category.id = :categoryId) AND " +
                        "(:keywords IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keywords, '%'))) AND " +
                        "r.isApproved = true AND r.isDeleted = false")
        Page<Restaurant> searchRestaurants(@Param("keywords") String keywords,
                        @Param("categoryId") Long categoryId,
                        Pageable pageable);

        @Override
        @NonNull
        @EntityGraph(attributePaths = { "category", "owner" })
        Optional<Restaurant> findById(@NonNull Long id);

        Page<Restaurant> findByIsApprovedFalse(Pageable pageable);

        @EntityGraph(attributePaths = { "category", "owner" })
        List<Restaurant> findByIsApprovedFalseAndIsDeletedFalse();

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Transactional
        @Query("UPDATE Restaurant r SET r.isApproved = true WHERE r.id = :id")
        void approveRestaurant(@Param("id") Long id);

        @EntityGraph(attributePaths = { "category" })
        List<Restaurant> findByOwnerId(Long ownerId);

        long countByIsApprovedTrue();
}
