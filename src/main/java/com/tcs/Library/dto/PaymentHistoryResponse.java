package com.tcs.Library.dto;

import com.tcs.Library.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponse {

    private String paymentId;
    private String receiptNumber;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private int fineCount;
}
