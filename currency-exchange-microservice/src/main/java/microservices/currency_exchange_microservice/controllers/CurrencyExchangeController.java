package microservices.currency_exchange_microservice.controllers;

import microservices.currency_exchange_microservice.repo.CurrencyExchangeRepository;
import microservices.currency_exchange_microservice.models.CurrencyExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class CurrencyExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyExchangeController.class);
    @Autowired
    private Environment environment;
    @Autowired
    private CurrencyExchangeRepository repository;
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retrieveExchangeValue(
            @PathVariable String from,
            @PathVariable String to)
    {
        //INFO [currency-exchange,5f0aef9106ddd2d2f551a32e0792824e,13bd6e6f662c092d]
        // 20844 --- [currency-exchange] [nio-8000-exec-4] [5f0aef9106ddd2d2f551a32e0792824e-13bd6e6f662c092d]
        // m.c.c.CurrencyExchangeController         : from USD to INR

        logger.info("from {} to {}", from, to);
//        CurrencyExchange currencyExchange = new CurrencyExchange(1000L, from, to, BigDecimal.valueOf(50));
        CurrencyExchange currencyExchange = repository.findByFromAndTo(from, to);
        if(currencyExchange== null)
        {
            throw new RuntimeException("Unable to find data for " + from + " to " + to);
        }
        // this is how we get the port that iam operating on laveraging the spring environment object
        currencyExchange.setEnvironment(environment.getProperty("local.server.port"));
         return currencyExchange;
    }

}
