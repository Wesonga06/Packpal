package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Handles weather data fetching from OpenWeatherMap API.
 */
public class WeatherService {

    private static final String API_KEY = "5e0067ca6bb07812d104885a27b479de"; // Your actual API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    /**
     * Fetches the current weather data for a given city.
     *
     * @param city The city name
     * @return WeatherData object containing the weather details
     */
    public WeatherData getCurrentWeather(String city) {
        try {
            String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            WeatherData data = new WeatherData();

            data.setCity(json.getString("name"));
            data.setCountry(json.getJSONObject("sys").getString("country"));
            data.setTemperature(json.getJSONObject("main").getDouble("temp"));
            data.setFeelsLike(json.getJSONObject("main").getDouble("feels_like"));
            data.setHumidity(json.getJSONObject("main").getInt("humidity"));
            data.setWindSpeed(json.getJSONObject("wind").getDouble("speed"));
            data.setDescription(json.getJSONArray("weather").getJSONObject(0).getString("description"));

            return data;

        } catch (Exception e) {
            System.out.println("Error fetching weather: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns formatted weather summary text for a given city.
     * This method wraps getCurrentWeather() and does not affect existing logic.
     */
    public String getWeather(String city) {
        WeatherData data = getCurrentWeather(city);
        if (data == null) {
            return "Unable to fetch weather data for " + city + ".";
        }

        return String.format(
            "%s, %s: %.1f°C (Feels like %.1f°C), %d%% humidity, %.1f m/s wind, %s %s",
            data.getCity(),
            data.getCountry(),
            data.getTemperature(),
            data.getFeelsLike(),
            data.getHumidity(),
            data.getWindSpeed(),
            data.getDescription(),
            data.getWeatherEmoji()
        );
    }
}
