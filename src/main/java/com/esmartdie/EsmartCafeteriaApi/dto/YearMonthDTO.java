package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Data
public class YearMonthDTO {

    private String yearMonth;

    public YearMonth getYearMonth() {
        try {
            return YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid yearMonth format, expected YYYY-MM format.");
        }
    }

    public void setYearMonth(String yearMonthStr) {
        this.yearMonth = yearMonthStr;
    }
}
