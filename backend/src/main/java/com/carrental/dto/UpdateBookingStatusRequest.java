package com.carrental.dto;

import com.carrental.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for administrative booking status updates.
 */
public class UpdateBookingStatusRequest {

    @NotNull(message = "Status is required")
    private BookingStatus status;

    public UpdateBookingStatusRequest() {
    }

    public UpdateBookingStatusRequest(BookingStatus status) {
        this.status = status;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
