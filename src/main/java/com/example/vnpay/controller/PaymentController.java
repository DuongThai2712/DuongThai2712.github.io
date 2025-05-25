package com.example.vnpay.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.vnpay.model.PaymentRequest;
import com.example.vnpay.service.VnPayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final VnPayService vnPayService;

    @PostMapping("/vnpay")
    public ResponseEntity<String> createVnPayPayment(@RequestBody PaymentRequest request, HttpServletRequest httpServletRequest) {
        String paymentUrl = vnPayService.createPaymentUrl(request, httpServletRequest);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/vnpay/return")
    public RedirectView handleReturn(@RequestParam Map<String, String> params) {
        if (vnPayService.verifyReturnData(params)) {
            // Optional: cập nhật DB
        }
        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        return new RedirectView("/return.html?" + queryString);
    }
}
