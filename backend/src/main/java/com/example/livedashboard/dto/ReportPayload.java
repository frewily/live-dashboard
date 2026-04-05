package com.example.livedashboard.dto;

public class ReportPayload {
    private String appId;
    private String windowTitle;
    private Long timestamp;
    private ExtraInfo extra;

    public ReportPayload() {}

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ExtraInfo getExtra() {
        return extra;
    }

    public void setExtra(ExtraInfo extra) {
        this.extra = extra;
    }

    public static class ExtraInfo {
        private Integer batteryPercent;
        private Boolean batteryCharging;

        public Integer getBatteryPercent() {
            return batteryPercent;
        }

        public void setBatteryPercent(Integer batteryPercent) {
            this.batteryPercent = batteryPercent;
        }

        public Boolean getBatteryCharging() {
            return batteryCharging;
        }

        public void setBatteryCharging(Boolean batteryCharging) {
            this.batteryCharging = batteryCharging;
        }
    }
}
