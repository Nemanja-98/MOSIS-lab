package elfakrs.mosis.vitaminc.myplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.telecom.Conference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.myplaces.databinding.ActivityMyPlacesMapsBinding;
import elfakrs.mosis.vitaminc.myplaces.models.MyPlace;
import elfakrs.mosis.vitaminc.myplaces.presenters.MyPlacesData;

public class MyPlacesMapsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMyPlacesMapsBinding binding;
    private MapView map = null;
    private IMapController mapController = null;
    private MyLocationNewOverlay myLocationOverlay = null;
    private ItemizedIconOverlay myPlacesOverlay = null;


    static int NEW_PLACE = 1;
    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private int state = 0;
    private boolean selCoorsEnabled = false;
    private GeoPoint placeLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Intent map_intent = getIntent();
            Bundle mapBundle = map_intent.getExtras();
            if(mapBundle != null)
            {
                state = mapBundle.getInt("state");
                if(state == CENTER_PLACE_ON_MAP)
                {
                    String placeLat = mapBundle.getString("lat");
                    String placeLon= mapBundle.getString("lon");
                    placeLoc = new GeoPoint(Double.parseDouble(placeLat), Double.parseDouble(placeLon));
                }
            }
        }
        catch (Exception e)
        {
            Log.d("Error", "Error reading state");
        }

        binding = ActivityMyPlacesMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_my_places_maps);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_place_intent = new Intent(MyPlacesMapsActivity.this, EditMyPlaceActivity.class);
                startActivityForResult(add_place_intent, NEW_PLACE);
            }
        });

        if(state != SELECT_COORDINATES)
        {
            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent edit_intent = new Intent(MyPlacesMapsActivity.this, EditMyPlaceActivity.class);
                    startActivityForResult(edit_intent, NEW_PLACE);
                }
            });
        }
        else
        {
            ViewGroup layout = (ViewGroup)binding.fab.getParent();
            if(layout != null)
                layout.removeView(binding.fab);
        }

        Context context = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        map = (MapView)findViewById(R.id.map);
        map.setMultiTouchControls(true);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
//            setMyLocationOverlay();
//            setOnMapClickOverlay();
            setupMap();
        }
        mapController = map.getController();
        if(mapController != null)
        {
            mapController.setZoom(15.0);
            GeoPoint startPoint = new GeoPoint(43.3209, 21.8958);
            mapController.setCenter(startPoint);
        }
    }

    private void setCenterPlaceOnMap()
    {
        mapController = map.getController();
        if(mapController != null)
        {
            mapController.setZoom(15.0);
            mapController.animateTo(placeLoc);
        }
    }

    private void setupMap()
    {
        switch (state)
        {
            case SHOW_MAP:
                setMyLocationOverlay();
                break;
            case SELECT_COORDINATES:{
                mapController = map.getController();
                if(mapController != null)
                {
                    mapController.setZoom(15.0);
                    mapController.setCenter(new GeoPoint(43.3209, 21.8958));
                }
                setOnMapClickOverlay();
                break;
            }
            case CENTER_PLACE_ON_MAP:
            default:
                setCenterPlaceOnMap();
                break;
        }
//        showMyPlaces();
    }

    private void setMyLocationOverlay()
    {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);
        mapController = map.getController();
        if(mapController != null)
        {
            mapController.setZoom(15.0);
            myLocationOverlay.enableFollowLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
//                    setMyLocationOverlay();
//                    setOnMapClickOverlay();
                    setupMap();
                }
                return;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_my_places_maps);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(state == SELECT_COORDINATES && !selCoorsEnabled)
        {
            menu.add(0, 1, 1, "Select coordinates");
            menu.add(0, 2, 2, "Cancel");
            return super.onCreateOptionsMenu(menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.menu_my_places_maps, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(state == SELECT_COORDINATES && !selCoorsEnabled)
        {
            if(id == 1)
            {
                selCoorsEnabled = true;
                Toast.makeText(this, "Select coordinates", Toast.LENGTH_SHORT).show();
            }
            else if (id == 2)
            {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
        else {
            if (id == R.id.action_new_place_item) {
                Intent edit_intent = new Intent(this, EditMyPlaceActivity.class);
                startActivityForResult(edit_intent, NEW_PLACE);
            } else if (id == R.id.action_about_item) {
                Intent about_intent = new Intent(this, About.class);
                startActivity(about_intent);
            } else if (id == android.R.id.home)
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnMapClickOverlay(){
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(state == SELECT_COORDINATES && selCoorsEnabled)
                {
                    String lon = Double.toString(p.getLongitude());
                    String lat = Double.toString(p.getLatitude());
                    Intent locationIntent = new Intent();
                    locationIntent.putExtra("lon", lon);
                    locationIntent.putExtra("lat", lat);
                    setResult(Activity.RESULT_OK, locationIntent);
                    finish();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                String lon = Double.toString(p.getLongitude());
                String lat = Double.toString(p.getLatitude());
                Intent locationIntent = new Intent();
                locationIntent.putExtra("lon",lon);
                locationIntent.putExtra("lat",lat);
                setResult(Activity.RESULT_OK, locationIntent);
                finish();
                return false;
            }
        };
        MapEventsOverlay overlayEvents = new MapEventsOverlay(mReceive);
        map.getOverlays().add(overlayEvents);
    }

    private void showMyPlaces()
    {
        if(myPlacesOverlay != null)
            map.getOverlays().remove(myPlacesOverlay);
        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for(int i = 0; i < MyPlacesData.getInstance().getMyPlaces().size(); i++)
        {
            MyPlace place = MyPlacesData.getInstance().getMyPlaces().get(i);
            OverlayItem item = new OverlayItem(place.getName(), place.getDescription(), new GeoPoint(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())));
            item.setMarker(this.getResources().getDrawable(R.drawable.ic_place_black_24));
            items.add(item);
        }
        myPlacesOverlay = new ItemizedIconOverlay<OverlayItem>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                Intent intent = new Intent(MyPlacesMapsActivity.this,ViewMyPlaceActivity.class);
                intent.putExtra("position",index);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                Intent intent = new Intent(MyPlacesMapsActivity.this,EditMyPlaceActivity.class);
                intent.putExtra("position",index);
                startActivityForResult(intent,2);
                return true;
            }
        }, getApplicationContext());
        map.getOverlays().add(myPlacesOverlay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}