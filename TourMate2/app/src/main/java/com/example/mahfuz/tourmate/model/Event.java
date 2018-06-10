package com.example.mahfuz.tourmate.model;

import java.io.Serializable;

public class Event implements Serializable{
    private String eventId;
    private String eventName;
    private String startingLocation;
    private String destination;
    private String departureDate;
    private String estimateBudget;
    private String creationDate;


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getEstimateBudget() {
        return estimateBudget;
    }

    public void setEstimateBudget(String estimateBudget) {
        this.estimateBudget = estimateBudget;
    }
}
