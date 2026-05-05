// dto/ReportResponse.java
package com.ritvik.zodiacverseBackend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReportResponse {
    private UUID id;
    private UUID kundliId;
    private String kundliPersonName;
    private String type;
    private String title;
    private String status;
    private BigDecimal price;
    private Integer pages;
    private LocalDateTime createdAt;
}