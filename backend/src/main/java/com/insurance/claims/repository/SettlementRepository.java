package com.insurance.claims.repository;

import com.insurance.claims.model.Claim;
import com.insurance.claims.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByClaim(Claim claim);
}
