package com.lostandfound.repository;

import com.lostandfound.model.ClaimedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimedItemRepository extends JpaRepository<ClaimedItem, Long> {
    
}

