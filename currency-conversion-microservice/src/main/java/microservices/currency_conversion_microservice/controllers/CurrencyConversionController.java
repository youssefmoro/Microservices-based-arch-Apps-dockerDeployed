package microservices.currency_conversion_microservice.controllers;

import microservices.currency_conversion_microservice.models.CurrencyConversion;
import microservices.currency_conversion_microservice.proxies.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("/v1")
public class CurrencyConversionController {
    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private Environment environment;
    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{qty}")
    public CurrencyConversion retrieveConversionValue(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal qty) {
        // the goal from this map is extracting out the path variables from the other application
        // and we have the exact same key string passed in the url of the other application
        //so this response entity now contains the response from the other application and we can extract the body from it
        //and the TO and FROM are present directly inside the response entity
        HashMap<String,String> uriVariables = new HashMap<>();
        uriVariables.put("FROM",from);
        uriVariables.put("TO",to);
        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/v1/currency-exchange/from/{FROM}/to/{TO}",CurrencyConversion.class,uriVariables);
        CurrencyConversion currencyExchanePassedMappedToCurrencyConversion = responseEntity.getBody();
     CurrencyConversion currencyConversion= new CurrencyConversion(
             currencyExchanePassedMappedToCurrencyConversion.getId(),
             from, to, qty,
             currencyExchanePassedMappedToCurrencyConversion.getConversionMultiple(),
            qty.multiply(currencyExchanePassedMappedToCurrencyConversion.getConversionMultiple()),
             currencyExchanePassedMappedToCurrencyConversion.getEnvironment());
        return currencyConversion;
    }
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{qty}")
    public CurrencyConversion retrieveConversionValueFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal qty) {
        CurrencyConversion currencyExchanePassedMappedToCurrencyConversion = currencyExchangeProxy.retrieveExchangeValue(from,to);
        CurrencyConversion currencyConversion= new CurrencyConversion(
                currencyExchanePassedMappedToCurrencyConversion.getId(),
                from, to, qty,
                currencyExchanePassedMappedToCurrencyConversion.getConversionMultiple(),
                qty.multiply(currencyExchanePassedMappedToCurrencyConversion.getConversionMultiple()),
                currencyExchanePassedMappedToCurrencyConversion.getEnvironment()+ " " + "feign");
        return currencyConversion;
    }
}
