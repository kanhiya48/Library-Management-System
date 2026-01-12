package com.tcs.Library.entity;

import java.time.LocalDateTime;

import com.tcs.Library.enums.BookStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_cpy_id")
    private Long id;
    @Column(name = "copy_pub_id")
    private String copypubId;
    private LocalDateTime issueTime;
    private LocalDateTime returnTime;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.FIRST;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void generatePublicId() {
        if (copypubId == null) {
            copypubId = book.getPublicId() + "_"
                    + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        }
    }
}
