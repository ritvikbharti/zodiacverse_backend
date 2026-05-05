package com.ritvik.zodiacverseBackend.model;

public enum PaymentStatus {
    CREATED,      // order created, payment not done yet
    SUCCESS,      // payment verified and wallet credited
    FAILED        // payment failed or verification failed
}