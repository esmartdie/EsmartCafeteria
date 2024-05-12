package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericApiResponseDTO {

    private boolean success;
    private String message;
    private Object data;
}
