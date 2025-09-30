package com.mockapi.repository;

import com.mockapi.model.MockEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MockEndpointRepository extends JpaRepository<MockEndpoint, String> {
    Optional<MockEndpoint> findByMethodAndPath(String method, String path);
}
