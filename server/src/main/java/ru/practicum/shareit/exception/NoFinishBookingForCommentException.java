package ru.practicum.shareit.exception;

public class NoFinishBookingForCommentException extends RuntimeException {

    public NoFinishBookingForCommentException(String message) {
        super(message);
    }
}