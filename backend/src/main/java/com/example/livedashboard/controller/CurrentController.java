package com.example.livedashboard.controller;

import com.example.livedashboard.dto.CurrentResponse;
import com.example.livedashboard.dto.DeviceState;
import com.example.livedashboard.service.DeviceStateService;
import com.example.livedashboard.component.VisitorTracker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CurrentController {

    private final VisitorTracker visitorTracker;
    private final DeviceStateService deviceStateService;

    public CurrentController(VisitorTracker visitorTracker, DeviceStateService deviceStateService) {
        this.visitorTracker = visitorTracker;
        this.deviceStateService = deviceStateService;
    }

    @GetMapping("/current")
    public CurrentResponse getCurrent(
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {

        String clientIp = getClientIp(request);
        visitorTracker.heartbeat(clientIp, userAgent);

        List<DeviceState> devices = deviceStateService.getAllDevices();

        return CurrentResponse.builder()
                .devices(devices)
                .serverTime(Instant.now().toString())
                .viewerCount(visitorTracker.getCount())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("CF-Connecting-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Forwarded-For");
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
