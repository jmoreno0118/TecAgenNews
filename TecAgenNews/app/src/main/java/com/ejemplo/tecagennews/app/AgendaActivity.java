package com.ejemplo.tecagennews.app;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Calendar;


public class AgendaActivity extends ActionBarActivity {

    public Calendar month;
    public CalendarAdapter adapter;
    public Handler handler;
    public ArrayList<String> items; // container to store some random calendar items

    LinearLayout rLayout;
    JSONArray dias;
    JSONArray eventos;
    String dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        //Instanciamos la variable month con al fecha del sistema
        month = Calendar.getInstance();

        //Se declara el LinearLayout para la lista de eventos
        rLayout = (LinearLayout) findViewById(R.id.text);

        //ArrayList para las fechas seleccionadas
        items = new ArrayList<String>();

        //Adaptador del Calendario
        adapter = new CalendarAdapter(this, month);

        //GridView para el calendario en el que se fija el adaptador
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        //Se crea un handler para manipular el objeto Runnable
        handler = new Handler();

        //Se obtienen las fechas con evento, de Internet
        obtenerTextoInternet();

        //Se obtiene el TextView del título y se fija el título del mes
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        //Se genera el boton para regresar de mes y se cre su accion de click
        TextView previous = (TextView) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Si el mes es el mes minimo, se reduce el año en uno, de lo contrario se reduce el mes
                if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR) - 1), month.getActualMaximum(Calendar.MONTH), 1);
                } else {
                    month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
                }
                //Se limpia el layout con la lista de eventos
                if ((rLayout).getChildCount() > 0) {
                    (rLayout).removeAllViews();
                }
                //Se refresca el calendario
                refreshCalendar();
            }
        });

        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Si el mes es el mes maximo, se aumenta el año en uno, de lo contrario se aumenta el mes
                if (month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR) + 1), month.getActualMinimum(Calendar.MONTH), 1);
                } else {
                    month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
                }
                if ((rLayout).getChildCount() > 0) {
                    (rLayout).removeAllViews();
                }
                refreshCalendar();
            }
        });

        //Se crea el click de los elementos del gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if ((rLayout).getChildCount() > 0) {
                    (rLayout).removeAllViews();
                }
                //Se obtiene el objeto textview del gridview
                TextView date = (TextView) v.findViewById(R.id.date);
                if (date instanceof TextView && !date.getText().equals("")) {
                    //Se obtiene el numero del dia
                    String day = date.getText().toString();
                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    String days = "";
                    if(day.length()==1) {
                        days = "-0"+day;
                    }else{
                        days = "-"+day;
                    }
                    dia = android.text.format.DateFormat.format("yyyy-MM", month)+days;
                    //Se obtienen los eventos del dia
                    obtenerEventosInternet();
                }

            }
        });
    }

    public void refreshCalendar() {
        TextView title = (TextView) findViewById(R.id.title);
        //Se refrescan los días mostrados en el gridview
        adapter.refreshDays();
        //Se notifica al adaptador que se modifico la informacion
        adapter.notifyDataSetChanged();
        obtenerTextoInternet();
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
            items.clear();
            try {
                for (int i = 0; i < dias.length(); i++) {
                    //Se añaden los dias con eventos al objeto item
                    items.add(dias.getJSONObject(i).getString("Dias"));
                }
            }catch(Exception e){}

            //Se le dan los items al adaptador
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };

    private void obtenerTextoInternet() {
        if (isNetworkAvailable()) {
            GetAPI getAPI = new GetAPI();
            getAPI.execute();
        } else {
            Toast.makeText(this, "Sin Conexion", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            } else {
                Toast.makeText(this, "Sin Conexion", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {}
        return isAvailable;
    }

    private class GetAPI extends AsyncTask<Object, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Object... objects) {
            int responseCode = -1;
            String resultado = "";
            JSONArray jsonResponse = null;
            try{
                URL apiURL = new URL("http://www.codipaj.com/itchihuahuaii/eq4/dias.php?month="+((month.get(Calendar.MONTH)) + 1)+"&year="+month.get(Calendar.YEAR));
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
                    dias = new JSONArray(resultado);
                }
            }catch(MalformedURLException e){}
            catch(IOException e){}
            catch(Exception e){}
            return jsonResponse;
        }

        protected void onPostExecute(JSONArray s){
            handler.post(calendarUpdater);
        }
    }

    private void obtenerEventosInternet() {
        if (isNetworkAvailable()) {
            GetAPIEvents getAPI = new GetAPIEvents();
            getAPI.execute();
        } else {
            Toast.makeText(this, "Sin Conexion", Toast.LENGTH_LONG).show();
        }
    }

    private class GetAPIEvents extends AsyncTask<Object, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Object... objects) {
            int responseCode = -1;
            String resultado = "";
            JSONArray jsonResponse = null;
            try{

                URL apiURL = new URL("http://www.codipaj.com/itchihuahuaii/eq4/eventos.php?date="+dia);
                Log.i("Eventos", "http://www.codipaj.com/itchihuahuaii/eq4/eventos.php?date=" + dia);
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
                    eventos = new JSONArray(resultado);
                }
            }catch(MalformedURLException e){}
            catch(IOException e){}
            catch(Exception e){}
            return jsonResponse;
        }

        protected void onPostExecute(JSONArray s){
            TextView rowTextView = new TextView(AgendaActivity.this);
            //Se se encuentran eventos en el día seleccionado, se muestran, de lo contrario, solo sale "Sin eventos"
            if(eventos.length() > 0){
                //Se crea y agrega text view, con cierto texto y color
                rowTextView.setText("Eventos:");
                rowTextView.setTextColor(Color.BLACK);
                rLayout.addView(rowTextView);
                try {
                    for (int i = 0; i < eventos.length(); i++) {
                        rowTextView = new TextView(AgendaActivity.this);
                        rowTextView.setText(eventos.getJSONObject(i).getString("Evento"));
                        rowTextView.setTextColor(Color.BLACK);
                        rLayout.addView(rowTextView);
                    }
                }catch(Exception e){}
            }else{
                rowTextView.setText("Sin eventos");
                rowTextView.setTextColor(Color.BLACK);
                rLayout.addView(rowTextView);
            }
        }
    }
}
