package services;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class WeatherService {
    // verified OpenWeatherMap API key
    private static final String API_KEY = "5e0067ca6bb07812d104885a27b479de";
    
    // Base URLs
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";

    /**
     * Fetches current weather data for a given city.
     */
    public WeatherData getCurrentWeather(String city) {
        try {
            String urlString = BASE_URL + "?q=" + URLEncoder.encode(city, "UTF-8")
                    + "&appid=" + API_KEY + "&units=metric";

            String jsonResponse = makeAPICall(urlString);
            if (jsonResponse != null) {
                return parseWeatherData(jsonResponse);
            }
        } catch (Exception e) {
            System.err.println("[WeatherService] Error fetching current weather: " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches forecast weather data for a given city (up to N days).
     */
    public WeatherForecast getWeatherForecast(String city, int days) {
        try {
            String urlString = FORECAST_URL + "?q=" + URLEncoder.encode(city, "UTF-8")
                    + "&appid=" + API_KEY + "&units=metric&cnt=" + (days * 8); // 8 forecasts/day

            String jsonResponse = makeAPICall(urlString);
            if (jsonResponse != null) {
                return parseForecastData(jsonResponse);
            }
        } catch (Exception e) {
            System.err.println("[WeatherService] Error fetching forecast: " + e.getMessage());
        }
        return null;
    }

    /**
     * Makes the HTTP GET call to the API endpoint.
     */
    private String makeAPICall(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                System.err.println("[WeatherService] API Response Code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("[WeatherService] API Call Failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parses JSON data from current weather endpoint.
     */
    private WeatherData parseWeatherData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            WeatherData data = new WeatherData();

            data.setCity(obj.getString("name"));
            data.setCountry(obj.getJSONObject("sys").getString("country"));
            data.setTemperature(obj.getJSONObject("main").getDouble("temp"));
            data.setFeelsLike(obj.getJSONObject("main").getDouble("feels_like"));
            data.setHumidity(obj.getJSONObject("main").getInt("humidity"));
            data.setWindSpeed(obj.getJSONObject("wind").getDouble("speed"));

            JSONArray weatherArray = obj.getJSONArray("weather");
            if (weatherArray.length() > 0) {
                JSONObject weather = weatherArray.getJSONObject(0);
                data.setDescription(weather.getString("description"));
                data.setIcon(weather.getString("icon"));
                data.setMain(weather.getString("main"));
            }

            return data;
        } catch (Exception e) {
            System.err.println("[WeatherService] Error parsing current weather JSON: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parses JSON data from forecast endpoint.
     */
    private WeatherForecast parseForecastData(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            WeatherForecast forecast = new WeatherForecast();

            JSONObject city = obj.getJSONObject("city");
            forecast.setCity(city.getString("name"));
            forecast.setCountry(city.getString("country"));

            JSONArray list = obj.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                WeatherData data = new WeatherData();

                data.setTimestamp(item.getLong("dt"));
                data.setTemperature(item.getJSONObject("main").getDouble("temp"));
                data.setHumidity(item.getJSONObject("main").getInt("humidity"));

                JSONArray weatherArray = item.getJSONArray("weather");
                if (weatherArray.length() > 0) {
                    JSONObject weather = weatherArray.getJSONObject(0);
                    data.setDescription(weather.getString("description"));
                    data.setIcon(weather.getString("icon"));
                    data.setMain(weather.getString("main"));
                }

                forecast.addForecast(data);
            }

            return forecast;
        } catch (Exception e) {
            System.err.println("[WeatherService] Error parsing forecast JSON: " + e.getMessage());
        }
        return null;
    }

    // ------------------------
    // Inner Data Model Classes
    // ------------------------

    /**
     * Represents current weather data.
     */
    public static class WeatherData {
        private String city;
        private String country;
        private double temperature;
        private double feelsLike;
        private int humidity;
        private double windSpeed;
        private String description;
        private String icon;
        private String main;
        private long timestamp;

        // Getters and setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public double getFeelsLike() { return feelsLike; }
        public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }

        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getMain() { return main; }
        public void setMain(String main) { this.main = main; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        // Adds a small visual cue (emoji)
        public String getWeatherEmoji() {
            if (main == null) return "☀️";
            switch (main.toLowerCase()) {
                case "clear": return "☀️";
                case "clouds": return "☁️";
                case "rain": return "🌧️";
                case "drizzle": return "🌦️";
                case "thunderstorm": return "⛈️";
                case "snow": return "❄️";
                case "mist":
                case "fog": return "🌫️";
                default: return "🌤️";
            }
        }
    }

    /**
     * Represents a multi-day weather forecast.
     */
    public static class WeatherForecast {
        private String city;
        private String country;
        private java.util.List<WeatherData> forecasts;

        public WeatherForecast() {
            forecasts = new java.util.ArrayList<>();
        }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public java.util.List<WeatherData> getForecasts() { return forecasts; }
        public void addForecast(WeatherData data) { forecasts.add(data); }
    }
}
