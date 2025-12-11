package org.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdoptionApplicationDTO {
    private Long id;
    private Long animalId;
    private String animalName;
    private Integer age;
    private String breed;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
}
