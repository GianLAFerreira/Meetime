package com.meetime.service;

import com.meetime.controller.model.request.WebHookRequest;

import java.util.List;

public interface WebHookService {

    void processReturn(List<WebHookRequest> events);
}
