package com.esmartdie.EsmartCafeteriaApi.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Data
public class YearMonthDTO {

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Invalid yearMonth format, expected YYYY-MM")
    private String yearMonth;

    public YearMonth getYearMonth() {
        return YearMonth.parse(yearMonth);
    }

    public void setYearMonth(String yearMonthStr) {
        this.yearMonth = yearMonthStr;
    }
}
