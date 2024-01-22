package com.leisa.microservice.individual.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class QueueMappingKey implements Serializable {
    private Long publisherId;
    private Long consumerId;
    private String eventType;

    public QueueMappingKey() {
    }

    public QueueMappingKey(Long publisherId, Long consumerId, String eventType) {
        this.publisherId = publisherId;
        this.consumerId = consumerId;
        this.eventType = eventType;
    }

    // Getters and Setters
    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueueMappingKey)) return false;
        QueueMappingKey that = (QueueMappingKey) o;
        return Objects.equals(getPublisherId(), that.getPublisherId()) &&
               Objects.equals(getConsumerId(), that.getConsumerId()) &&
               Objects.equals(getEventType(), that.getEventType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublisherId(), getConsumerId(), getEventType());
    }
}
