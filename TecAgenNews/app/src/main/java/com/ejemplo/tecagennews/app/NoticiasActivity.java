package com.ejemplo.tecagennews.app;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class NoticiasActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);

        obtenerTextoInternet();
    }

    private void obtenerTextoInternet(){
        if(isNetworkAvailable()){
            GetAPI getAPI = new GetAPI();
            getAPI.execute();
        }else{
            Toast.makeText(this, "Sin Conexion", Toast.LENGTH_LONG).show();
        }
    }

    private class GetAPI extends AsyncTask<Object, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Object ... objects){
            int responseCode = -1;
            String resultado = "";
            JSONArray jsonResponse = null;
            try{
                URL apiURL = new URL("http://www.codipaj.com/itchihuahuaii/eq4/noticias.php");
                HttpURLConnection httpConnection = (HttpURLConnection)apiURL.openConnection();
                httpConnection.connect();

                responseCode = httpConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = httpConnection.getInputStream();
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),8);
                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while((line = bReader.readLine()) != null){
                        sBuilder.append(line + "\n");
                    }
                    inputStream.close();
                    resultado = sBuilder.toString();

                    jsonResponse = new JSONArray(resultado);

                }
            }catch(MalformedURLException e){}
            catch(IOException e){}
            catch(Exception e){}

            return jsonResponse;
        }
        @Override
        protected  void onPostExecute(JSONArray s){
            ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

            try {
                for (int i = 0; i < s.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", s.getJSONObject(i).getString("Nombre"));
                    if(s.getJSONObject(i).getString("Descripcion") != "null"){
                        map.put("desc", s.getJSONObject(i).getString("Descripcion"));
                    }else{
                        map.put("desc", "");
                    }
                    mylist.add(map);
                }
            }catch(Exception e){}

            ListAdapter adapter = new SimpleAdapter(NoticiasActivity.this, mylist , android.R.layout.simple_list_item_2,
                    new String[] { "name", "desc" },
                    new int[] { android.R.id.text1, android.R.id.text2});

            setListAdapter(adapter);
        }
    }

    private boolean isNetworkAvailable(){
        boolean isAvailable = true;
        try{
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()){
                isAvailable = true;
            }
        } catch(Exception e){}
        return isAvailable;
    }
}
