package com.proiect.service.detection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.proiect.model.IpData;
import com.proiect.service.utils.ApiClient;
import com.proiect.service.utils.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class IpAnalysisService {
    private final CacheManager<IpData> cacheManager;
    private final ApiClient apiClient;
    private DatabaseReader maxMindReader;
    private static final Logger logger =  LoggerFactory.getLogger(IpAnalysisService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construieste un IpAnalysisService cu managerul de cache si clientul API dat.
     * @param cacheManager managerul de cache pentru date IP
     * @param apiClient clientul API pentru cereri HTTP
     * @throws IOException daca baza de date MaxMind nu poate fi incarcata
     */
    @Autowired
    public IpAnalysisService(CacheManager<IpData> cacheManager, ApiClient apiClient) throws IOException {
        this.cacheManager = cacheManager;
        this.apiClient = apiClient;
        ClassPathResource resource = new ClassPathResource("GeoLite2-City.mmdb");
        try (InputStream inputStream = resource.getInputStream()) {
            this.maxMindReader = new DatabaseReader.Builder(inputStream).build();
        }
    }
    /**
     * Analizeaza adresa IP asincron.
     * @param ip adresa IP de analizat
     * @return un CompletableFuture cu datele IP optionale
     */
    public CompletableFuture<Optional<IpData>> analyzeIpAsync(String ip){
        if(!isValidIp(ip)){
            logger.warn("Invalid IP: {}", ip);
            return CompletableFuture.completedFuture(Optional.empty());
        }

        var cached = cacheManager.get(ip);
        if(cached != null){
            logger.debug("IP {} found in cache", ip);
            return CompletableFuture.completedFuture(Optional.of(cached));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                var data = fetchFromApi(ip).or(() -> fetchFromMaxMind(ip));
                data.ifPresent(d -> {
                    d.setIsVpn(isVpn(ip));
                    cacheManager.put(ip, d);
                });
                return data;
            } catch (Exception e) {
                logger.error("Error analyzing IP {}: {}", ip, e.getMessage());
                return Optional.empty();
            }
        });
    }
    /**
     * Analizeaza adresa IP sincron.
     * @param ip adresa IP de analizat
     * @return datele IP optionale
     */
    public Optional<IpData> analyzeIp(String ip) {
        try {
            return analyzeIpAsync(ip).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Timeout or error for IP {}: {}", ip, e.getMessage());
            return Optional.empty();
        }
    }
    /**
     * Preia date IP din API.
     * @param ip adresa IP
     * @return datele IP optionale
     */
    private Optional<IpData> fetchFromApi(String ip) {
        var url = "http://ip-api.com/json/%s".formatted(ip);
        var headers = Map.of("User-Agent", "ThreatMap/1.0");
        var responseOpt = apiClient.get(url, headers, 3);
        if (responseOpt.isPresent()) {
            try {
                var json = objectMapper.readTree(responseOpt.get());
                return Optional.of(new IpData(
                        ip,
                        json.path("country").asText("Unknown"),
                        json.path("city").asText("Unknown"),
                        json.path("lat").asDouble(0.0),
                        json.path("lon").asDouble(0.0),
                        json.path("isp").asText("Unknown"),
                        json.path("org").asText("Unknown"),
                        json.path("proxy").asBoolean(false)
                ));
            } catch (Exception e) {
                logger.warn("JSON parse error for {}: {}", ip, e.getMessage());
            }
        }
        return Optional.empty();
    }
    /**
     * Preia date IP din baza de date MaxMind.
     * @param ip adresa IP
     * @return datele IP optionale
     */
    private Optional<IpData> fetchFromMaxMind(String ip) {
        try {
            var inetAddress = InetAddress.getByName(ip);
            var response = maxMindReader.city(inetAddress);
            var country = response.getCountry().getName();
            var city = response.getCity().getName();
            var location = response.getLocation();
            return Optional.of(new IpData(
                    ip,
                    country != null ? country : "Unknown",
                    city != null ? city : "Unknown",
                    location.getLatitude(),
                    location.getLongitude(),
                    "Unknown",
                    "Unknown",
                    false
            ));
        } catch (Exception e) {
            logger.warn("MaxMind fallback failed for {}: {}", ip, e.getMessage());
        }
        return Optional.empty();
    }
    /**
     * Verifica daca IP-ul este VPN.
     * @param ip adresa IP
     * @return true daca VPN, false altfel
     */
    private boolean isVpn(String ip) {
        try {
            var inetAddress = InetAddress.getByName(ip);
            var response = maxMindReader.city(inetAddress);
            return response.getTraits().isAnonymousProxy();
        } catch (Exception e) {
            logger.warn("VPN check failed for {}: {}", ip, e.getMessage());
            return false;
        }
    }
    /**
     * Valideaza formatul adresei IP.
     * @param ip sirul IP
     * @return true daca valid, false altfel
     */
    private boolean isValidIp(String ip) {
        return ip != null && ip.matches("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
    }
}
