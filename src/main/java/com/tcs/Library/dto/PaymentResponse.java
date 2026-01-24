package com.tcs.Library.dto;

import com.tcs.Library.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private String paymentId;
    private String receiptNumber;
    private String transactionId;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;

    // User information
    private String userName;
    private String userEmail;

    // Fine information
    private List<Long> fineIds;
    private List<FineDetail> fineDetails;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FineDetail {
        private Long fineId;
        private BigDecimal amount;
        private String reason;
    }
}
