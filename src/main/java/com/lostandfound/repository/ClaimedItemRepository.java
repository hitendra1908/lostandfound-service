package com.lostandfound.repository;

import com.lostandfound.model.ClaimedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimedItemRepository extends JpaRepository<ClaimedItem, Long> {
    List<ClaimedItem> findByUserId(Long userId);
}

