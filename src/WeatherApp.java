import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// get weather data from api
public class WeatherApp {
    // get weather data from api for precise location
    public static JSONObject getWeatherData(String locationName){
        // get location from geolocation api
        JSONArray locationData = getLocationData(locationName);

        System.out.println("Searching for location: " + locationName);


        if (locationData == null || locationData.isEmpty()) {
            System.out.println("No location found for: " + locationName);
            return null;
        }

        // pull latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build request URL with coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" + "latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,weather_code,wind_speed_10m&wind_speed_unit=mph&temperature_unit=fahrenheit&precipitation_unit=inch";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);

            // 200 = successful connection
            System.out.println("Requesting weather from: " + urlString);
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // store json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            // parse data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            

            // get hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // get index of current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // make json weather object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("windspeed", windspeed);

            return weatherData;


        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // pulls coordinates for given location
    public static JSONArray getLocationData(String locationName){

        System.out.println("Entered getLocationData with: " + locationName);

        // change spaces to + to make api work
        locationName = locationName.replaceAll(" ", "+");

        // create api url with location
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";
        System.out.println("Location API URL: " + urlString);

        
        try{
        // call api
        HttpURLConnection conn = fetchApiResponse(urlString);

        // 200 is successful connection
        if(conn.getResponseCode() != 200){
            System.out.println("Error: connection to API failed");
            return null;
        }else{
            // show results
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            // store json data in string builder
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());

    
            }

            scanner.close();

            // disconnect url
            conn.disconnect();

            //parse json data into object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // get location data from location name
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return locationData;
        }
        }catch(Exception e){
            System.out.println("Exception in getLocationData:");
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        // create connection
        try{    
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // connect to api
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }


        // failed connection
        return null;
    }


    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // find matching time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        // current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
    
        // format date to match api
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

         // format and print date and time
         String formattedDateTime = currentDateTime.format(formatter);

         return formattedDateTime;
    }

    // make weather code readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "Clear";
        }else if(weathercode <= 3L && weathercode > 0L){
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)){
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}

