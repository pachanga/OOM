package com.example.oom;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ComponentCallbacks2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTrimMemory(int level) {
        Context context = getApplicationContext();
        Toast toast;
        int duration = Toast.LENGTH_SHORT;
        switch(level) {
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                toast = Toast.makeText(context, "onTrimMemory: Running Moderate", duration);
                toast.show();
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                toast = Toast.makeText(context, "onTrimMemory: Running Low", duration);
                toast.show();
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                toast = Toast.makeText(context, "onTrimMemory: Running Critical", duration);
                toast.show();
                break;
            default:
                toast = Toast.makeText(context, "onTrimMemory: Default", duration);
                toast.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}