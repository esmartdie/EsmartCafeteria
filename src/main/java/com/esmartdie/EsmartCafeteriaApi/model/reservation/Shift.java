package com.esmartdie.EsmartCafeteriaApi.model.reservation;

public enum Shift {

    DAY1("12:00-13:00"),
    DAY2("12:00-13:00"),
    DAY3("12:00-13:00"),
    DAY4("12:00-13:00"),
    NIGHT1("19:00-20:00"),
    NIGHT2("20:00-21:00"),
    NIGHT3("21:00-23:00"),
    NIGHT4("22:00-23:00");

    private final String timeRange;

    Shift(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getTimeRange() {
        return timeRange;
    }
}
