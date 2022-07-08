package com.example.whatmain;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherData {
    String[] output = new String[6];
    public String weather;
    public String temperature;
    public String humidity;
    public String is_rain ="0";
    public String rain_prob = null;
    public String rain_amount = null;

    public String[] getWeather(String nx, String ny, String baseDate, String baseTime, String type, int is_daytime) throws IOException, JSONException {
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "KoUHhFgcXAWFHvii7YKfxL2cdQMYE7j0dUoxZZXryPaJ9lz3HH463WOAopzv0XXAm66dHnxiUGjzj9Zk87ATCw%3D%3D";

        System.out.println(apiUrl);

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "12");
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        //System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        System.out.println("###result"+result);

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");
        System.out.println("### JSONARRAY:" + jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {
                if (fcstValue.equals("1")) {
                    weather = "맑음";
                } else if (fcstValue.equals("2")) {
                    weather = "비";
                } else if (fcstValue.equals("3")) {
                    weather = "구름많음";
                } else if (fcstValue.equals("4")) {
                    weather = "흐림";
                }
            }
            if (category.equals("PTY")) {
                if (fcstValue.equals("1")) {
                    weather = "비";
                    is_rain = "1";
                } else if (fcstValue.equals("3")) {
                    weather = "눈";
                }
            }
            if (category.equals("TMP")) {
                temperature = fcstValue;
            }

            // 추가 데이터
            if (category.equals("REH")) {
                humidity = fcstValue;
            }
            if (category.equals("PCP")){
                rain_amount = fcstValue;
            }
            if (category.equals("POP")) {
                rain_prob = fcstValue;
            }
        }

        if(is_rain.equals("1")) {
            if (rain_amount.equals("강수없음")) {
                rain_amount = "0.1mm 미만";
            }
        }
        if (is_daytime != 1) {
            weather += "_밤";
        }

        output[0] = temperature;
        output[1] = weather;
        output[2] = humidity;
        output[3] = is_rain;
        output[4] = rain_prob;
        output[5] = rain_amount;
        return output;
    }
}