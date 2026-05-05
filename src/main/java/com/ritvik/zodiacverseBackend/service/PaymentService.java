package com.ritvik.zodiacverseBackend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.ritvik.zodiacverseBackend.dto.*;
import com.ritvik.zodiacverseBackend.model.*;
import com.ritvik.zodiacverseBackend.repo.PaymentRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final WalletService walletService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.currency}")
    private String currency;

    @Transactional
    public CreateOrderResponse createOrder(String email, CreateOrderRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Razorpay needs amount in paise (1 rupee = 100 paise)
            long amountInPaise = req.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "receipt_" + UUID.randomUUID().toString().substring(0, 8));
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = order.get("id");

            // Save payment record with CREATED status
            Payment payment = Payment.builder()
                    .user(user)
                    .razorpayOrderId(razorpayOrderId)
                    .amount(req.getAmount())
                    .currency(currency)
                    .status(PaymentStatus.CREATED)
                    .build();

            paymentRepo.save(payment);

            log.info("Razorpay order created: {} for user: {}", razorpayOrderId, email);

            return CreateOrderResponse.builder()
                    .orderId(razorpayOrderId)
                    .amount(req.getAmount())
                    .amountInPaise(amountInPaise)
                    .currency(currency)
                    .keyId(keyId)
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new RuntimeException("Payment order creation failed: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse verifyAndCredit(String email, VerifyPaymentRequest req) {
        // Step 1: Find the payment record
        Payment payment = paymentRepo.findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        // Step 2: Verify user owns this payment
        if (!payment.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized payment verification");
        }

        // Step 3: Verify Razorpay signature (CRITICAL security check)
        try {
            String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();

            boolean isValid = Utils.verifyPaymentSignature(
                    new JSONObject()
                            .put("razorpay_order_id", req.getRazorpayOrderId())
                            .put("razorpay_payment_id", req.getRazorpayPaymentId())
                            .put("razorpay_signature", req.getRazorpaySignature()),
                    getKeySecret()
            );

            if (!isValid) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepo.save(payment);
                throw new RuntimeException("Payment signature verification failed");
            }

        } catch (RazorpayException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            throw new RuntimeException("Signature verification error: " + e.getMessage());
        }

        // Step 4: Update payment record
        payment.setRazorpayPaymentId(req.getRazorpayPaymentId());
        payment.setRazorpaySignature(req.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepo.save(payment);

        // Step 5: Credit wallet
        walletService.addMoney(email, WalletActionRequest.builder()
                .amount(payment.getAmount())
                .description("Wallet recharge via Razorpay — ₹" +
                        payment.getAmount().toPlainString())
                .build());

        log.info("Payment verified & wallet credited: {} → ₹{}",
                email, payment.getAmount());

        return toDto(payment);
    }

    public Page<PaymentResponse> history(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentRepo
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(this::toDto);
    }

    // We need the secret for signature verification
    @Value("${razorpay.key.secret}")
    private String keySecret;

    private String getKeySecret() {
        return keySecret;
    }

    private PaymentResponse toDto(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .razorpayOrderId(p.getRazorpayOrderId())
                .razorpayPaymentId(p.getRazorpayPaymentId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus().name())
                .createdAt(p.getCreatedAt())
                .build();
    }
}