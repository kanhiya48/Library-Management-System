package com.tcs.Library.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard paginated response with simplified structure.
 * Contains only essential pagination info: page, totalPages, totalElements, and
 * content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /** Current page number (0-indexed) */
    private int page;

    /** Total number of pages */
    private int totalPages;

    /** Total number of elements across all pages */
    private long totalElements;

    /** Number of elements in current page */
    private int size;

    /** Whether this is the first page */
    private boolean first;

    /** Whether this is the last page */
    private boolean last;

    /** The actual content/data for this page */
    private List<T> content;

    /**
     * Create PagedResponse from Spring Data Page object.
     */
    public static <T> PagedResponse<T> from(Page<T> page) {
        return PagedResponse.<T>builder()
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .content(page.getContent())
                .build();
    }
}
