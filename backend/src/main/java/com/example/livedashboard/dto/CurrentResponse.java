package com.example.livedashboard.dto;

import java.util.List;

public class CurrentResponse {
    private List<DeviceState> devices;
    private String serverTime;
    private int viewerCount;

    public CurrentResponse() {}

    public CurrentResponse(List<DeviceState> devices, String serverTime, int viewerCount) {
        this.devices = devices;
        this.serverTime = serverTime;
        this.viewerCount = viewerCount;
    }

    public List<DeviceState> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceState> devices) {
        this.devices = devices;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public int getViewerCount() {
        return viewerCount;
    }

    public void setViewerCount(int viewerCount) {
        this.viewerCount = viewerCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<DeviceState> devices;
        private String serverTime;
        private int viewerCount;

        public Builder devices(List<DeviceState> devices) {
            this.devices = devices;
            return this;
        }

        public Builder serverTime(String serverTime) {
            this.serverTime = serverTime;
            return this;
        }

        public Builder viewerCount(int viewerCount) {
            this.viewerCount = viewerCount;
            return this;
        }

        public CurrentResponse build() {
            return new CurrentResponse(devices, serverTime, viewerCount);
        }
    }
}
