package com.ejemplo.tecagennews.app;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Calendario");
        tab1.setIndicator("Calendario");
        Intent fistIntent = new Intent(this, AgendaActivity.class);
        tab1.setContent(fistIntent);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("Noticias");
        tab2.setIndicator("Noticias");
        Intent secondIntent = new Intent(this, NoticiasActivity.class);
        tab2.setContent(secondIntent);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("Nosotros");
        tab3.setIndicator("Nosotros");
        Intent thirdIntent = new Intent(this, UsActivity.class);
        tab3.setContent(thirdIntent);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
    }

}
