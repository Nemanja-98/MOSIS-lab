package elfakrs.mosis.vitaminc.myplaces;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import elfakrs.mosis.vitaminc.myplaces.databinding.ActivityViewMyPlaceBinding;
import elfakrs.mosis.vitaminc.myplaces.models.MyPlace;
import elfakrs.mosis.vitaminc.myplaces.presenters.MyPlacesData;

public class ViewMyPlaceActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityViewMyPlaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewMyPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_view_my_place);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        int position = -1;
        try{
            Intent intent = getIntent();
            Bundle positionBundle = intent.getExtras();
            position = positionBundle.getInt("position");
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        if(position>=0)
        {
            MyPlace place = MyPlacesData.getInstance().getPlace(position);
            TextView twName = (TextView)findViewById(R.id.viewMyPlace_textView_name);
            twName.setText(place.getName());
            TextView twDescription = (TextView)findViewById(R.id.viewMyPlace_textView_description);
            twDescription.setText(place.getDescription());
        }
        final Button btnFinished = (Button)findViewById(R.id.viewMyPlace_button_finished);
        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_view_my_place);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_show_map)
            Toast.makeText(this, "Show Map!", Toast.LENGTH_SHORT).show();
        else if(id == R.id.action_my_places_list)
        {
            Intent my_places_intent = new Intent(this, MyPlacesList.class);
            startActivity(my_places_intent);
        }
        else if(id == R.id.action_about)
        {
            Intent about_intent = new Intent(this, About.class);
            startActivity(about_intent);
        }
        else if(id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}