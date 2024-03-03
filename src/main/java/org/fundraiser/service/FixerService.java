package org.fundraiser.service;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.domain.EuroExchangeRate;
import org.fundraiser.dto.info.FixerResponse;
import org.fundraiser.repository.FixerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class FixerService {
    @Value("${fixer.api.key}")
    private String apiKey;

    @Value("${fixer.api.base_url}")
    private String baseUrl;

    private final FixerRepository fixerRepository;

    @Autowired
    public FixerService(FixerRepository fixerRepository) {
        this.fixerRepository = fixerRepository;
    }

    //    @PostConstruct // minden indításnál lefut!
    public void initExchangeRates() {
        getRates();
    }

    private String fixerUrl(String fromCurrency,
                            String toCurrency) {
        String endpoint = "latest";
        log.info(baseUrl + endpoint + "?access_key=" + apiKey + "&base=" + fromCurrency + "&symbols=" + toCurrency);
        return baseUrl + endpoint + "?access_key=" + apiKey + "&base=" + fromCurrency + "&symbols=" + toCurrency;
    }

    public double getCurrentEuroExchangeRate(String fromCurrency, String toCurrency, String amount) {
        RestTemplate restTemplate = new RestTemplate();
        String url = fixerUrl(fromCurrency, toCurrency);

        ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
        log.info("Raw JSON response: {}", rawResponse.getBody());

        ResponseEntity<FixerResponse> response = restTemplate.getForEntity(url, FixerResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Double> rates = response.getBody().getRates();
            double result = Double.parseDouble(amount) * rates.get(toCurrency);
            log.info("Conversion result: {}", result);
            return result;
        } else {
            log.error("Error in API call: {}", rawResponse.getStatusCode());
            return 0.0;
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void getRates() {
        EuroExchangeRate euroExchangeRate = new EuroExchangeRate();

        euroExchangeRate.setEUR(getCurrentEuroExchangeRate("EUR", "EUR", "1"));
        euroExchangeRate.setUSD(getCurrentEuroExchangeRate("EUR", "USD", "1"));
        euroExchangeRate.setHUF(getCurrentEuroExchangeRate("EUR", "HUF", "1"));
        euroExchangeRate.setLastUpdated(LocalDateTime.now());

        fixerRepository.save(euroExchangeRate);
    }

    public Double convertCurrencyFromEuro(String toCurrency, Double amount) {
        List<EuroExchangeRate> euroExchangeRates = fixerRepository.getEuroExchangeRate();

        Double rate;

        switch (toCurrency) {
            case "USD":
                rate = euroExchangeRates.get(0).getUSD();
                break;
            case "HUF":
                rate = euroExchangeRates.get(0).getHUF();
                break;
            case "EUR":
                rate = 1.0;
                break;
            default:
                throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }

        return amount * rate;
    }

    public Double convertCurrencyToEuro(String toCurrency, Double amount) {
        List<EuroExchangeRate> euroExchangeRates = fixerRepository.getEuroExchangeRate();

        Double rate;

        switch (toCurrency) {
            case "USD":
                rate = euroExchangeRates.get(0).getUSD();
                break;
            case "HUF":
                rate = euroExchangeRates.get(0).getHUF();
                break;
            case "EUR":
                rate = 1.0;
                break;
            default:
                throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }

        return amount / rate;
    }

}
