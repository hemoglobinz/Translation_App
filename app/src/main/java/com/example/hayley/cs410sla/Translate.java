package com.example.hayley.cs410sla;

/**
 * Created by niruiz3964 on 3/13/18.
 */
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class Translate {
    APIKey key = new APIKey();
    final private String GOOGLE_API_KEY = key.APIKey;


    public String translate(String text, String from, String to) {

        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://www.googleapis.com/language/translate/v2?key=" + GOOGLE_API_KEY + "&q=" + encodedText + "&target=" + to + "&source=" + from;
            Log.d("URL string", urlStr);

            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;

            //Check that we got got a SUC result
            if (conn.getResponseCode() == 200)
            {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            if (conn.getResponseCode() != 200) {
                System.err.println(conn.getResponseCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;

            //Get the data from the Buffer
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString().replace("&#39;", "'"));

            //Make sure that the Json object is valid
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();

               return configData(obj);
            }

        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    private String configData(JsonObject obj){
        if (obj.get("error") == null) {
            String translatedText = obj.get("data").
                    getAsJsonObject().
                    get("translations").getAsJsonArray().
                    get(0).getAsJsonObject().
                    get("translatedText").
                    getAsString();
            return translatedText;
        }
        else{
            return null;
        }
    }
}
