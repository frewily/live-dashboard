package com.example.livedashboard.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VisitorTracker {

    private static final long TIMEOUT_MS = 30_000;
    private static final int MAX_ENTRIES = 10_000;

    private final ConcurrentHashMap<String, Long> seen = new ConcurrentHashMap<>();

    private static final List<String> BOT_PATTERNS = List.of(
            "bot", "crawl", "spider", "slurp", "mediapartners",
            "facebookexternalhit", "linkedinbot", "twitterbot",
            "whatsapp", "telegrambot", "discordbot", "bingpreview",
            "yandex", "baidu", "sogou", "bytespider", "applebot",
            "amazonbot", "gptbot", "claudebot", "anthropic",
            "semrush", "ahref", "mj12bot", "dotbot", "petalbot",
            "dataforseo", "headlesschrome", "phantomjs", "puppeteer",
            "lighthouse", "pagespeed", "pingdom", "uptimerobot"
    );

    public void heartbeat(String ip, String userAgent) {
        if (ip == null || ip.isEmpty()) {
            return;
        }
        if (userAgent != null && isBot(userAgent)) {
            return;
        }
        if (!seen.containsKey(ip) && seen.size() >= MAX_ENTRIES) {
            cleanup();
            if (seen.size() >= MAX_ENTRIES) {
                return;
            }
        }
        seen.put(ip, System.currentTimeMillis());
    }

    public int getCount() {
        cleanup();
        return seen.size();
    }

    private boolean isBot(String userAgent) {
        String lower = userAgent.toLowerCase();
        return BOT_PATTERNS.stream().anyMatch(lower::contains);
    }

    @Scheduled(fixedRate = 30000)
    public void cleanup() {
        long cutoff = System.currentTimeMillis() - TIMEOUT_MS;
        seen.entrySet().removeIf(entry -> entry.getValue() < cutoff);
    }
}
