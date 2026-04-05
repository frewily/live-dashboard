package com.example.livedashboard.service;

import com.example.livedashboard.dto.DeviceState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceStateService {

    private static final long OFFLINE_TIMEOUT_MS = 60_000;

    private final ConcurrentHashMap<String, DeviceState> devices = new ConcurrentHashMap<>();

    public void updateDevice(DeviceState device) {
        device.setOnline(true);
        device.setLastSeenAt(System.currentTimeMillis());
        devices.put(device.getDeviceId(), device);
    }

    public List<DeviceState> getAllDevices() {
        cleanupOfflineDevices();
        return new ArrayList<>(devices.values());
    }

    public Optional<DeviceState> getDevice(String deviceId) {
        return Optional.ofNullable(devices.get(deviceId));
    }

    @Scheduled(fixedRate = 30000)
    public void cleanupOfflineDevices() {
        long cutoff = System.currentTimeMillis() - OFFLINE_TIMEOUT_MS;
        devices.entrySet().removeIf(entry -> {
            DeviceState device = entry.getValue();
            Long lastSeen = device.getLastSeenAt();
            return lastSeen == null || lastSeen < cutoff;
        });
    }
}
