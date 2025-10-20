package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherService {

    private static final String API_KEY = "5e0067ca6bb07812d104885a27b479de";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    // Method 1: Fetch weather (simple text summary)
    public static String getWeather(String city) {
        try {
            String urlStr = String.format(API_URL, city, API_KEY);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            double temp = json.getJSONObject("main").getDouble("temp");
            String condition = json.getJSONArray("weather").getJSONObject(0).getString("description");
            return String.format("🌤 %s: %.1f°C (%s)", city, temp, condition);
        } catch (Exception e) {
            return "⚠️ Unable to fetch weather for " + city;
        }
    }

    // Method 2: Return a WeatherData object (for DashboardView)
    public WeatherData getCurrentWeather(String city) {
        try {
            String urlStr = String.format(API_URL, city, API_KEY);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

            return new WeatherData(
                    json.getString("name"),
                    json.getJSONObject("sys").getString("country"),
                    main.getDouble("temp"),
                    main.getDouble("feels_like"),
                    main.getInt("humidity"),
                    wind.getDouble("speed"),
                    weather.getString("description")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //  class WeatherData
    public static class WeatherData {
        private String city;
        private String country;
        private double temperature;
        private double feelsLike;
        private int humidity;
        private double windSpeed;
        private String description;

        public WeatherData(String city, String country, double temperature, double feelsLike,
                           int humidity, double windSpeed, String description) {
            this.city = city;
            this.country = country;
            this.temperature = temperature;
            this.feelsLike = feelsLike;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.description = description;
        }

        public String getCity() { return city; }
        public String getCountry() { return country; }
        public double getTemperature() { return temperature; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }
        public String getDescription() { return description; }

        public String getWeatherEmoji() {
            String d = description.toLowerCase();
            if (d.contains("rain")) return "🌧";
            if (d.contains("cloud")) return "☁️";
            if (d.contains("clear")) return "☀️";
            if (d.contains("snow")) return "❄️";
            return "🌍";
        }
    }
}

