package com.ejemplo.tecagennews.app;

/**
 * Created by Jesus Moreno on 31/05/2014.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter {
    static final int FIRST_DAY_OF_WEEK =0; // Sunday = 0, Monday = 1

    private Context mContext;

    private Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;

    public CalendarAdapter(Context c, Calendar monthCalendar) {
        month = monthCalendar;
        selectedDate = (Calendar)monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for(int i = 0;i != items.size();i++){
            if(items.get(i).length()==1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);
        }
        dayView = (TextView)v.findViewById(R.id.date);

        //Se les asigna el fondo a todos los view del gridview
        v.setBackgroundResource(R.drawable.item_backgroundw);

        //Se fija el dia en el textView
        dayView.setText(days[position]);

        String date = days[position];

        if(date.length()==1) {
            date = "0"+date;
        }
        String monthStr = ""+(month.get(Calendar.MONTH)+1);
        if(monthStr.length()==1) {
            monthStr = "0"+monthStr;
        }
        //Si el dia existe en nuestro arreglo de eventos, se le cambia el fondo a uno rojo
        if(date.length()>0 && items!=null && items.contains(date)) {
            v.setBackgroundResource(R.drawable.item_background_focusedb);
        }
        return v;
    }

    public void refreshDays(){
        items.clear();

        //Se obtienen ultimo y primer dia del mes
        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = (int)month.get(Calendar.DAY_OF_WEEK);

        //Se genera arreglo de String, de acuerdo al tamaño de dias
        if(firstDay==1){
            days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
            days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }

        int j=FIRST_DAY_OF_WEEK;

        //Genera los días vacios antes del primer dia del mes
        if(firstDay>1) {
            for(j=0;j<firstDay-FIRST_DAY_OF_WEEK;j++) {
                days[j] = "";
            }
        }
        else {
            for(j=0;j<FIRST_DAY_OF_WEEK*6;j++) {
                days[j] = "";
            }
            j=FIRST_DAY_OF_WEEK*6+1;
        }

        //Se rellena el string de los dias
        int dayNumber = 1;
        for(int i=j-1;i<days.length;i++) {
            days[i] = ""+dayNumber;
            dayNumber++;
        }
    }

    // references to our items
    public String[] days;
}
