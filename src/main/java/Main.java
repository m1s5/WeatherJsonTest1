import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("TOKEN");
        String lon = "37.6173";
        String lat = "55.7558";

        String response = getResponse(token, lon, lat);

        double max = getMorningMax(response);
        double avg = getMorningAvg(response);

        System.out.println("\nAverage morning temp: " + avg + "\n" +
        "Max morning temp: " + max);
    }

    public static String getResponse(String token, String lon, String lat){

        StringBuilder response;

        String query = "https://api.openweathermap.org/data/2.5/onecall?" +
                "lat=" + lat +
                "&lon=" + lon +
                "&exclude=minutely,current,hourly,alerts" +
                "&appid=" + token + "&units=metric";

        HttpURLConnection connection = null;

        response = new StringBuilder();
        try{

            connection = (HttpURLConnection) new URL(query).openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(2500);
            connection.setReadTimeout(2500);

            connection.connect();


            if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                String line;
                while ((line = in.readLine()) != null){
                    response.append(line);
                    response.append("\n");
                }

            } else{
                System.out.println("fail:" + connection.getResponseCode() + connection.getResponseMessage());
            }

        } catch (Throwable cause){
            cause.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return response.toString();
    }

    public static double getMorningMax(String response){
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dailyArray = jsonObject.getJSONArray("daily");

        double max = dailyArray.getJSONObject(0).getJSONObject("temp").getDouble("morn");

        for(int i = 0; i < 5; i++){
            double cur = dailyArray.getJSONObject(i).getJSONObject("temp").getDouble("morn");
            if (max < cur) max = cur;
        }
        return max;
    }

    public static double getMorningAvg(String response){
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dailyArray = jsonObject.getJSONArray("daily");

        double avg = 0;

        for(int i = 0; i < 5; i++){
            avg += dailyArray.getJSONObject(i).getJSONObject("temp").getDouble("morn");
        }
        return avg/5;
    }

}
