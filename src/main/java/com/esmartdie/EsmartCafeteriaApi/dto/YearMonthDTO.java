package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

import java.time.YearMonth;

@Data
public class YearMonthDTO {

    private YearMonth yearMonth;

    public YearMonth getYearMonth() {
        return yearMonth;
    }
    public void setYearMonth(String yearMonthStr) {
        this.yearMonth = YearMonth.parse(yearMonthStr);
    }
}
