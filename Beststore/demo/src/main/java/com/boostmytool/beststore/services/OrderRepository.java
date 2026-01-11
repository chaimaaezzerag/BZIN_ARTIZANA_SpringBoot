package com.boostmytool.beststore.services;

import com.boostmytool.beststore.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
