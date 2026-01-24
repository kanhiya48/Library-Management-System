package com.tcs.Library.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FineResponse {
    private Long id;
    private BigDecimal amount;
    private boolean isPaid;
    private LocalDate createdAt;
    private LocalDate paidAt;

    // Book details
    private String bookTitle;
    private LocalDate issuedDate;
    private LocalDate dueDate;
}
