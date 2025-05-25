package com.example.vnpay.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String orderInfo;
}