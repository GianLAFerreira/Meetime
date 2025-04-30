package com.meetime.service;

import com.meetime.controller.model.request.WebHookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebHookServiceImpl implements WebHookService{


    @Override
    public void processReturn(List<WebHookRequest> events) {
        log.info("events {}", events);
        for (WebHookRequest event : events) {
            if ("contact.creation".equalsIgnoreCase(event.getSubscriptionType())) {
                log.info("Novo contato criado: id={} em {}", event.getObjectId(), event.getOccurredAt());
                //aqui da pra salver no banco, mas como n estava na doc eu n fiz
            } else {
                log.info("Evento ignorado: {}", event.getSubscriptionType());
            }
        }
    }
}
