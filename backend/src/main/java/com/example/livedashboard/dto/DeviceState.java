package com.example.livedashboard.dto;

public class DeviceState {
    private String deviceId;
    private String deviceName;
    private String platform;
    private String appName;
    private String displayTitle;
    private boolean online;
    private Long lastSeenAt;

    public DeviceState() {}

    public DeviceState(String deviceId, String deviceName, String platform, 
                       String appName, String displayTitle, boolean online, Long lastSeenAt) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.platform = platform;
        this.appName = appName;
        this.displayTitle = displayTitle;
        this.online = online;
        this.lastSeenAt = lastSeenAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Long getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Long lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String deviceId;
        private String deviceName;
        private String platform;
        private String appName;
        private String displayTitle;
        private boolean online;
        private Long lastSeenAt;

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder deviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder platform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder displayTitle(String displayTitle) {
            this.displayTitle = displayTitle;
            return this;
        }

        public Builder online(boolean online) {
            this.online = online;
            return this;
        }

        public Builder lastSeenAt(Long lastSeenAt) {
            this.lastSeenAt = lastSeenAt;
            return this;
        }

        public DeviceState build() {
            return new DeviceState(deviceId, deviceName, platform, appName, displayTitle, online, lastSeenAt);
        }
    }
}
