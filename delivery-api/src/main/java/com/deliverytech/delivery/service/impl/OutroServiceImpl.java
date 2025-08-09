package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.service.OutroService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OutroServiceImpl implements OutroService {

    private final RestTemplate restTemplate;

    public OutroServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String chamaOutroServico() {
        return restTemplate.getForObject("http://outro-servico/api/data", String.class);
    }
}