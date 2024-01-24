package com.leisa.microservice.consume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leisa.microservice.consume.model.QueueMapping;
import com.leisa.microservice.consume.model.QueueMappingKey;

import java.util.List;

@Repository
public interface QueueMappingRepository extends JpaRepository<QueueMapping, QueueMappingKey> {
    List<QueueMapping> findByIdPublisherIdAndIdEventType(Long publisherId, String eventType);
}
