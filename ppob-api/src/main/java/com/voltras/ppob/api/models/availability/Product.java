package com.voltras.ppob.api.models.availability;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
public record Product(@NotBlank String group, @NotBlank String type) {
}