package com.meetime.controller.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateContactResponse {

    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String company;
}
