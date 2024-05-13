package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientDTO {

    private String name;
    private String lastName;
    private String email;
}
