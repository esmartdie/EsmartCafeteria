package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class CalendarCreationResponseDTO {

    private String yearMonth;
    private int recordCount;

    public CalendarCreationResponseDTO(String yearMonth, int recordCount) {
        this.yearMonth = yearMonth;
        this.recordCount = recordCount;
    }

    @Override
    public String toString() {
        return "CalendarCreationResponse{" +
                "yearMonth='" + yearMonth + '\'' +
                ", recordCount=" + recordCount +
                '}';
    }
}
