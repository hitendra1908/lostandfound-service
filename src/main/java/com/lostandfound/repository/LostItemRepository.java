package com.lostandfound.repository;

import com.lostandfound.model.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {

}

