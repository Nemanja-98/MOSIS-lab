package elfakrs.mosis.vitaminc.myplaces;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyPlacesList extends AppCompatActivity {
    private ArrayList<String> _myPlacesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        _myPlacesList =  new ArrayList<String>();
        _myPlacesList.add("Park Sv. Save");
        _myPlacesList.add("Cair");
        _myPlacesList.add("Bubanj");
        _myPlacesList.add("Gradska Basta, Nis");

        ListView my_places_list = (ListView)findViewById(R.id.listView_my_places_list);
        my_places_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _myPlacesList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_show_map_item)
            Toast.makeText(this, "Show map!", Toast.LENGTH_SHORT).show();
        if(id == R.id.action_new_place_item)
            Toast.makeText(this, "New place!", Toast.LENGTH_SHORT).show();
        if(id == R.id.action_about_item)
        {
            Intent about_intent = new Intent(this, About.class);
            startActivity(about_intent);
        }

        return super.onOptionsItemSelected(item);
    }
}