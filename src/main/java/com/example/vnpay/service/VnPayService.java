package com.example.vnpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.vnpay.model.PaymentRequest;
import com.example.vnpay.util.VnPayUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VnPayService {

    @Value("${vnpay.paymentUrl}")
    private String vnpayUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    public String createPaymentUrl(PaymentRequest request, HttpServletRequest servletRequest) {
        String vnp_TxnRef = request.getOrderId();
        String vnp_OrderInfo = request.getOrderInfo();
        String vnp_Amount = String.valueOf(request.getAmount().multiply(BigDecimal.valueOf(100)).intValue());

        String vnp_IpAddr = servletRequest.getRemoteAddr();
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", vnp_Amount);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", vnp_OrderInfo);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", vnp_IpAddr);
        vnpParams.put("vnp_CreateDate", vnp_CreateDate);

        String queryUrl = VnPayUtil.buildUrlWithSecureHash(vnpParams, secretKey);
        return vnpayUrl + "?" + queryUrl;
    }

    public boolean verifyReturnData(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        Map<String, String> filteredParams = new HashMap<>(params);
        filteredParams.remove("vnp_SecureHash");
        filteredParams.remove("vnp_SecureHashType");

        String computedHash = VnPayUtil.calculateHash(filteredParams, secretKey);
        return receivedHash != null && receivedHash.equals(computedHash);
    }
}