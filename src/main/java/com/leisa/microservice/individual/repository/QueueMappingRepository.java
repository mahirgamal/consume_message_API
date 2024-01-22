package com.leisa.microservice.individual.repository;

import com.leisa.microservice.individual.model.QueueMapping;
import com.leisa.microservice.individual.model.QueueMappingKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueMappingRepository extends JpaRepository<QueueMapping, QueueMappingKey> {
    List<QueueMapping> findByIdPublisherIdAndIdEventType(Long publisherId, String eventType);
}
