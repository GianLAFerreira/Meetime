package com.meetime.controller.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebHookRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long subscriptionId;

    @NotNull
    private Long portalId;

    @NotNull
    private String subscriptionType;

    @NotNull
    private Long objectId;

    @NotNull
    private Long occurredAt;
}
