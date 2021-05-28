package elfakrs.mosis.vitaminc.myplaces;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import elfakrs.mosis.vitaminc.myplaces.databinding.ActivityEditMyPlaceBinding;
import elfakrs.mosis.vitaminc.myplaces.databinding.FragmentFirst2Binding;
import elfakrs.mosis.vitaminc.myplaces.models.MyPlace;
import elfakrs.mosis.vitaminc.myplaces.presenters.MyPlacesData;

public class EditMyPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityEditMyPlaceBinding binding;
    private boolean editMode = true;
    private int position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditMyPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_edit_my_place);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        try{
            Intent listIntent = getIntent();
            Bundle positionBundle = listIntent.getExtras();
            if(positionBundle != null)
                position = positionBundle.getInt("position");
            else
                editMode = false;
        }
        catch (Exception e)
        {
            editMode = false;
        }

        Button btn_finish = (Button)findViewById(R.id.editMyPlace_button_finish);
        btn_finish.setOnClickListener(this);

        btn_finish.setEnabled(false);
        EditText et_name = (EditText)findViewById(R.id.editMyPlace_editText_name);
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btn_finish.setEnabled(s.length()>0);
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.editMyPlace_button_cancel);
        btn_cancel.setOnClickListener(this);

        if(!editMode)
            btn_finish.setEnabled(false);
        else if(position>=0)
        {
            btn_finish.setText("Save");
            MyPlace place = MyPlacesData.getInstance().getPlace(position);
            et_name.setText(place.getName());
            EditText et_description = (EditText)findViewById(R.id.editMyPlace_editText_description);
            et_description.setText(place.getDescription());
        }
        Button btn_location = (Button)findViewById(R.id.editMyPlace_button_location);
        btn_location.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_edit_my_place);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.editMyPlace_button_finish: {
                EditText et_name = (EditText)findViewById(R.id.editMyPlace_editText_name);
                String name = et_name.getText().toString();
                EditText et_description = (EditText)findViewById(R.id.editMyPlace_editText_description);
                String description = et_description.getEditableText().toString();
                EditText latEdit = (EditText)findViewById(R.id.editMyPlace_editText_latitude);
                String lat = latEdit.getText().toString();
                EditText lonEdit = (EditText)findViewById(R.id.editMyPlace_editText_longitude);
                String lon = lonEdit.getText().toString();
                if(!editMode) {
                    MyPlace place = new MyPlace(name, description);
                    place.setLatitude(lat);
                    place.setLongitude(lon);
                    MyPlacesData.getInstance().addNewPlace(place);
                }
                else{
                    MyPlace place = MyPlacesData.getInstance().getPlace(position);
                    place.setName(name);
                    place.setDescription(description);
                    place.setLatitude(lat);
                    place.setLongitude(lon);
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
            case R.id.editMyPlace_button_cancel: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.editMyPlace_button_location:{
                Intent map_intent = new Intent(this, MyPlacesMapsActivity.class);
                map_intent.putExtra("state", MyPlacesMapsActivity.SELECT_COORDINATES);
                startActivityForResult(map_intent, 1);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_my_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_show_map)
        {
            Intent map_intent = new Intent(this, MyPlacesMapsActivity.class);
            map_intent.putExtra("state", MyPlacesMapsActivity.SHOW_MAP);
            startActivity(map_intent);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                String lon = data.getExtras().getString("lon");
                EditText lonText = (EditText) findViewById(R.id.editMyPlace_editText_longitude);
                lonText.setText(lon);
                String lat = data.getExtras().getString("lat");
                EditText latText = (EditText) findViewById(R.id.editMyPlace_editText_latitude);
                latText.setText(lat);
            }
        }
        catch (Exception e){
                //todo: handle exception
            }
        }
}