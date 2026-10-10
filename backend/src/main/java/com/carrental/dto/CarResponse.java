package com.carrental.dto;

import com.carrental.entity.Car;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing car response data.
 * Prevents direct exposure of JPA entities in the REST API layer.
 */
public class CarResponse {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String registrationNumber;
    private BigDecimal pricePerDay;
    private boolean available;
    private String imageUrl;
    private LocalDateTime createdAt;

    public CarResponse() {
    }

    public CarResponse(Long id, String brand, String model, Integer year, String color,
                       String registrationNumber, BigDecimal pricePerDay, boolean available,
                       String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    /**
     * Factory method to convert a Car JPA entity to CarResponse DTO.
     *
     * @param car the source entity
     * @return the transformed CarResponse DTO or null
     */
    public static CarResponse fromEntity(Car car) {
        if (car == null) {
            return null;
        }
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getRegistrationNumber(),
                car.getPricePerDay(),
                car.isAvailable(),
                car.getImageUrl(),
                car.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
