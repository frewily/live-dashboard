package com.example.livedashboard.controller;

import com.example.livedashboard.dto.DeviceState;
import com.example.livedashboard.dto.ReportPayload;
import com.example.livedashboard.service.DeviceStateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {

    private final DeviceStateService deviceStateService;

    private static final String DEVICE_ID_HEADER = "X-Device-Id";
    private static final String DEVICE_NAME_HEADER = "X-Device-Name";
    private static final String PLATFORM_HEADER = "X-Platform";

    public ReportController(DeviceStateService deviceStateService) {
        this.deviceStateService = deviceStateService;
    }

    @PostMapping("/report")
    public String report(
            HttpServletRequest request,
            @RequestHeader(value = DEVICE_ID_HEADER, required = false, defaultValue = "default-device") String deviceId,
            @RequestHeader(value = DEVICE_NAME_HEADER, required = false, defaultValue = "My Device") String deviceName,
            @RequestHeader(value = PLATFORM_HEADER, required = false, defaultValue = "windows") String platform,
            @RequestBody(required = false) ReportPayload payload) {

        DeviceState device = new DeviceState();
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceName);
        device.setPlatform(platform);

        if (payload != null) {
            device.setAppName(payload.getAppId() != null ? payload.getAppId() : "Unknown");
            device.setDisplayTitle(payload.getWindowTitle() != null ? payload.getWindowTitle() : "");
        } else {
            device.setAppName("Unknown");
            device.setDisplayTitle("");
        }

        deviceStateService.updateDevice(device);

        return "OK";
    }
}
