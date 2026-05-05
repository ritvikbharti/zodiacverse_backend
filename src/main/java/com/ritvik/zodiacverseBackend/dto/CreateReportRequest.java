// dto/CreateReportRequest.java
package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CreateReportRequest {

    @NotNull(message = "Kundli ID required")
    private UUID kundliId;

    @NotBlank(message = "Report type required")
    private String type;   // CAREER / LOVE / HEALTH / FINANCE / COMPLETE
}