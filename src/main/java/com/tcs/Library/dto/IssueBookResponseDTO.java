package com.tcs.Library.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueBookResponseDTO {
    private String bookTitle;
    private String bookCopyPublicId;
    private String userPublicId;
    private String userName;
    private LocalDateTime issueTime;
    private LocalDateTime returnTime; // Due Date
    private String status;
}
