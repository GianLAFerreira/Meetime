package com.meetime.controller;

import com.meetime.controller.model.request.WebHookRequest;
import com.meetime.service.WebHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebHookService webHookService;

    @PostMapping("/contact")
    public void handleContactWebhook(@RequestBody List<WebHookRequest> events) {
        webHookService.processReturn(events);
    }
}
