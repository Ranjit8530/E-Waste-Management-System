package com.ewaste.repository;

import com.ewaste.entity.PickupRequest;
import com.ewaste.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    Page<PickupRequest> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("""
            SELECT r FROM PickupRequest r
            WHERE (:status IS NULL OR r.status = :status)
              AND (:location IS NULL OR lower(r.location) = lower(:location))
              AND (:keyword IS NULL OR lower(r.user.name) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(r.user.email) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(r.deviceType) LIKE lower(concat('%', :keyword, '%')))
            ORDER BY r.createdAt DESC
            """)
    Page<PickupRequest> findAllByFilters(@Param("status") RequestStatus status,
                                         @Param("location") String location,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);

    long countByStatus(RequestStatus status);

    long countByLocationIgnoreCase(String location);

    long countByLocationIgnoreCaseAndStatus(String location, RequestStatus status);
}
