package com.meetime.service.mapper;

import com.meetime.controller.model.request.CreateContactRequest;
import com.meetime.controller.model.response.CreateContactResponse;
import com.meetime.integration.hubspot.model.request.CreateContactHubsPotRequest;
import com.meetime.integration.hubspot.model.response.CreateContactHubsPotResponse;
import com.meetime.service.vo.CreateContactCreateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    CreateContactCreateVO toCreateContactCreate(CreateContactRequest request);

    CreateContactHubsPotRequest toCreateContactCreateVO(CreateContactCreateVO createContactCreateVO);

    @Mapping(target = "email",            source = "properties.email")
    @Mapping(target = "firstname",        source = "properties.firstname")
    @Mapping(target = "lastname",         source = "properties.lastname")
    @Mapping(target = "phone",            source = "properties.phone")
    @Mapping(target = "company",          source = "properties.company")
    CreateContactResponse toResponse(CreateContactHubsPotResponse clientResponse);
}