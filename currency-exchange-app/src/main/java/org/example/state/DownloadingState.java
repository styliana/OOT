package org.example.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ExchangeApp;
import org.example.model.ExchangeRate;
import org.example.model.ExchangeTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadingState extends AppState {

    private static final String API_URL = "https://api.nbp.pl/api/exchangerates/tables/A/?format=json";

    public DownloadingState(ExchangeApp app) {
        super(app);
    }

    @Override
    public void downloadData() throws NoSuchAlgorithmException, KeyManagementException {
        try {
            // Pobierz dane z API
            RestTemplate restTemplate = createRestTemplateWithTrustAll();
            ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);

            // Parsuj JSON używając Jackson ObjectMapper
            ExchangeTable table = parseNBPResponse(response.getBody());

            // Zapisz tabelę w aplikacji
            app.setExchangeTable(table);

            // Zmień stan na Ready
            app.setState(new ReadyState(app));

            System.out.println("Dane pobrane pomyślnie! (" + table.getRates().size() + " kursów)");

        } catch (Exception e) {
            System.err.println("Błąd podczas pobierania danych: " + e.getMessage());
            app.setState(new ErrorState(app, e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Parsuje JSON z API NBP używając Jackson ObjectMapper
     */
    private ExchangeTable parseNBPResponse(String json) throws Exception {
        // Utwórz ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        // Parsuj JSON jako JsonNode (drzewo JSON)
        JsonNode root = mapper.readTree(json);

        // JSON jest tablicą, więc pobierz pierwszy element [0]
        JsonNode tableNode = root.get(0);

        // Pobierz podstawowe dane z tabeli
        String tableId = tableNode.get("no").asText();           // np. "199/A/NBP/2025"
        String dateStr = tableNode.get("effectiveDate").asText(); // np. "2025-10-14"

        // Przekonwertuj datę String na Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date timestamp = sdf.parse(dateStr);

        // Utwórz obiekt ExchangeTable
        ExchangeTable table = new ExchangeTable(tableId, timestamp);

        // Pobierz tablicę kursów
        JsonNode ratesArray = tableNode.get("rates");

        // Iteruj przez wszystkie kursy i dodaj do tabeli
        for (JsonNode rateNode : ratesArray) {
            String currency = rateNode.get("currency").asText();
            String code = rateNode.get("code").asText();
            double mid = rateNode.get("mid").asDouble();

            // Utwórz obiekt ExchangeRate i dodaj do tabeli
            ExchangeRate rate = new ExchangeRate(currency, code, mid);
            table.addRate(rate);
        }

        return table;
    }

    private static RestTemplate createRestTemplateWithTrustAll()
            throws NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        return new RestTemplate();
    }
}