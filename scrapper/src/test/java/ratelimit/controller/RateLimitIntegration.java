package ratelimit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public interface RateLimitIntegration {
    // Вспомогательный метод для установки IP-адреса

    default RequestPostProcessor remoteAddr(String remoteAddr) {
        return request -> {
            request.setRemoteAddr(remoteAddr);
            return request;
        };
    }
}
