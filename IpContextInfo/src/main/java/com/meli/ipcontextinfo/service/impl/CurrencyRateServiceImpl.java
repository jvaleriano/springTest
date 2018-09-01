package com.meli.ipcontextinfo.service.impl;

import com.meli.ipcontextinfo.model.RateResponseCurrencyCode;
import com.meli.ipcontextinfo.service.CurrencyRateService;
import com.meli.ipcontextinfo.service.model.CurrencyRateDto;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 *
 * @author VAL13448
 */
@Service
public class CurrencyRateServiceImpl implements CurrencyRateService{
    
    private static final Logger LOG = Logger.getLogger(CurrencyRateServiceImpl.class.getName());

    private RestTemplate restTemplate = null;

    /**
     * Spring inject 
     * @param builder
     * @param url system parameter "url.currencyRate" default value https://data.fixer.io/api
     * @param apiKey
     */
    public CurrencyRateServiceImpl(RestTemplateBuilder builder, @Value("${url.currencyRate:http://data.fixer.io/api}") String url, @Value("${url.currencyRate.apiKey:606b069d85d65954dad92f822baa7805}") String apiKey) {
        this.restTemplate = builder.build();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(url+"/latest?access_key=606b069d85d65954dad92f822baa7805"));
    }

    @Override
    public BigDecimal getCurrencyRate(String currency , RateResponseCurrencyCode responseCurrencyCode) {
        try {
            CurrencyRateDto res =  restTemplate.getForObject("&base={currency}&symbols={responseCurrencyCode}", CurrencyRateDto.class,currency,responseCurrencyCode.toString());
            if(res != null && res.isSuccess()){
                return res.getRates().values().iterator().next();
            }
        } catch (RestClientException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    @Override
     public BigDecimal getCurrencyDolarRate(String currency) {
         return getCurrencyRate(currency, RateResponseCurrencyCode.USD);
     }
     
     
     /*
     valid: {"success":true,"timestamp":1535747349,"base":"EUR","date":"2018-08-31","rates":{"USD":1.160867}}
     
     invalid: {"success":false,"error":{"code":105,"type":"base_currency_access_restricted"}}
     */
}