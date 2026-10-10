package com.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object providing availability check results for a car and date range.
 */
public class AvailabilityResponse {

    private Long carId;
    private String brand;
    private String model;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean available;
    private long rentalDays;
    private BigDecimal pricePerDay;
    private BigDecimal estimatedTotalPrice;
    private String message;

    public AvailabilityResponse() {
    }

    public AvailabilityResponse(Long carId, String brand, String model,
                                LocalDate startDate, LocalDate endDate, boolean available,
                                long rentalDays, BigDecimal pricePerDay,
                                BigDecimal estimatedTotalPrice, String message) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.startDate = startDate;
        this.endDate = endDate;
        this.available = available;
        this.rentalDays = rentalDays;
        this.pricePerDay = pricePerDay;
        this.estimatedTotalPrice = estimatedTotalPrice;
        this.message = message;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(long rentalDays) {
        this.rentalDays = rentalDays;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getEstimatedTotalPrice() {
        return estimatedTotalPrice;
    }

    public void setEstimatedTotalPrice(BigDecimal estimatedTotalPrice) {
        this.estimatedTotalPrice = estimatedTotalPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
