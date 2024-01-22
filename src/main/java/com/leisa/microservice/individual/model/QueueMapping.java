package com.leisa.microservice.individual.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "queue_mapping")
public class QueueMapping {

    @EmbeddedId
    private QueueMappingKey id;

    private String consumerQueuename;

    public QueueMapping() {
    }

    // Constructors
    public QueueMapping(QueueMappingKey id, String consumerQueuename) {
        this.id = id;
        this.consumerQueuename = consumerQueuename;
    }

    // Getters and Setters
    public QueueMappingKey getId() {
        return id;
    }

    public void setId(QueueMappingKey id) {
        this.id = id;
    }

    public String getConsumerQueuename() {
        return consumerQueuename;
    }

    public void setConsumerQueuename(String consumerQueuename) {
        this.consumerQueuename = consumerQueuename;
    }
}
