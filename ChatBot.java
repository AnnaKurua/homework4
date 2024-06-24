package org.example.chatbot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Scanner;

public class ChatBot {
    private static final String WEATHER_API_KEY = "086ca6d296323472c76b2c494f8537d7";

    private static final String EXCHANGE_RATE_API_KEY = "e6abb97245c94ab71b469143";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the ChatBot!");
        while (true) {
            System.out.println("Enter 'weather' to get current weather or 'exchange' to get currency exchange rate. Type 'exit' to quit.");
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("exit")) {
                break;
            } else if (input.equals("weather")) {
                System.out.println("Enter the city name:");
                String city = scanner.nextLine();
                try {
                    String weather = getWeather(city);
                    System.out.println("Current weather in " + city + ": " + weather);
                } catch (IOException e) {
                    System.out.println("Error fetching weather data: " + e.getMessage());
                }
            } else if (input.equals("exchange")) {
                System.out.println("Enter the base currency (e.g., USD):");
                String baseCurrency = scanner.nextLine().toUpperCase();
                System.out.println("Enter the target currency (e.g., EUR):");
                String targetCurrency = scanner.nextLine().toUpperCase();
                try {
                    String rate = getExchangeRate(baseCurrency, targetCurrency);
                    System.out.println("Exchange rate from " + baseCurrency + " to " + targetCurrency + ": " + rate);
                } catch (IOException e) {
                    System.out.println("Error fetching exchange rate data: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
        scanner.close();
    }

    private static String getWeather(String city) throws IOException {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + WEATHER_API_KEY + "&units=metric";
        return fetchData(url, "weather");
    }

    private static String getExchangeRate(String baseCurrency, String targetCurrency) throws IOException {
        String url = "https://v6.exchangerate-api.com/v6/" + EXCHANGE_RATE_API_KEY + "/latest/" + baseCurrency;
        return fetchData(url, targetCurrency);
    }

    private static String fetchData(String url, String key) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                if (key.equals("weather")) {
                    return jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString() +
                            ", temperature: " + jsonObject.getAsJsonObject("main").get("temp").getAsString() + "°C";
                } else {
                    return jsonObject.getAsJsonObject("conversion_rates").get(key).getAsString();
                }
            }
        }
    }
}

