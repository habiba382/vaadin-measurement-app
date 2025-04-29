package com.example.repository;

import com.example.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressReopsitory extends JpaRepository<Address, Long> {
}
