package iss.nus.com.logicuniversitystationary.JSON;

/**
 * Created by student on 2/9/15.
 */

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class JSONParser {

    final String TAG = "JsonParser.java";

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public static JSONArray getJSONArrayFromUrl(String url) {

        String line;

        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

            URL json = new URL(url);
            URLConnection jc = json.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));

            line = reader.readLine().toString();

            JSONArray jsonResponse = new JSONArray(line);

            reader.close();

            return jsonResponse;

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

        return null;
    }

    public static JSONObject getJSONObjectFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            try {
                // Get our response as a String.
                String jsonString = EntityUtils.toString(httpResponse.getEntity());


                // Parse the JSON String into a JSONArray object.
                return new JSONObject(jsonString);

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONObjectFromUrl(String url) {
        String line="";

        // Making HTTP request
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

            URL json = new URL(url);
            URLConnection jc = json.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));

            line = reader.readLine().toString();

            JSONObject jsonResponse = new JSONObject(line);

            reader.close();

            return jsonResponse;

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
        return null;
    }

    public static JSONObject getJSONObjectFromUrl(String url, String data) {
        String line="";

        try {
            line = postStream(url, data);
            JSONObject jsonResponse = new JSONObject(line);

            return jsonResponse;

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return null;
    }

    public static String postStream(String url, String data) {
        InputStream is = null;
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(data));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readStream(is);
    }

    static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            is.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return(sb.toString());
    }
}

