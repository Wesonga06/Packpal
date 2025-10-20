/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author cindy
 */
public class WeatherData {
    private String city;
    private String country;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String description;

    // --- Getters and Setters ---
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

    /**
     * Returns a simple emoji for common weather conditions.
     */
    public String getWeatherEmoji() {
        String desc = description.toLowerCase();
        if (desc.contains("rain")) return "🌧️";
        if (desc.contains("cloud")) return "☁️";
        if (desc.contains("sun") || desc.contains("clear")) return "☀️";
        if (desc.contains("storm")) return "⛈️";
        if (desc.contains("snow")) return "❄️";
        return "🌡️";
    }
}
