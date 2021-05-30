package elfakrs.mosis.vitaminc.myplaces.presenters;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import elfakrs.mosis.vitaminc.myplaces.models.MyPlace;

public class MyPlacesData {

    private ArrayList<MyPlace> myPlaces;
    private HashMap<String, Integer> myPlaceKeyIndexMapping;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD = "my-places";

    private MyPlacesData()
    {
        myPlaces = new ArrayList<MyPlace>();
        myPlaceKeyIndexMapping =  new HashMap<String, Integer>();
        database = FirebaseDatabase.getInstance("https://myplaces-mosis-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);
//        myPlaces.add(new MyPlace("Park Sv. Save"));
//        myPlaces.add(new MyPlace("Cair"));
//        myPlaces.add(new MyPlace("Bubanj"));
//        myPlaces.add(new MyPlace("Gradska Basta, Nis"));

    }

    ValueEventListener parentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {
            String myPlaceKey = dataSnapshot.getKey();
            if(!myPlaceKeyIndexMapping.containsKey(myPlaceKey))
            {
                MyPlace myPlace = dataSnapshot.getValue(MyPlace.class);
                myPlace.key = myPlaceKey;
                myPlaces.add(myPlace);
                myPlaceKeyIndexMapping.put(myPlaceKey, myPlaces.size() - 1);
            }
        }

        @Override
        public void onChildChanged(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {
            String myPlaceKey = dataSnapshot.getKey();
            MyPlace myPlace = dataSnapshot.getValue(MyPlace.class);
            myPlace.key = myPlaceKey;
            if(myPlaceKeyIndexMapping.containsKey(myPlaceKey))
            {
                int index = myPlaceKeyIndexMapping.get(myPlaceKey);
                myPlaces.set(index, myPlace);
            }
            else
            {
                myPlaces.add(myPlace);
                myPlaceKeyIndexMapping.put(myPlaceKey, myPlaces.size() - 1);
            }
        }

        @Override
        public void onChildRemoved(@NonNull @NotNull DataSnapshot dataSnapshot) {
            String myPlaceKey = dataSnapshot.getKey();
            if(myPlaceKeyIndexMapping.containsKey(myPlaceKey))
            {
                int index = myPlaceKeyIndexMapping.get(myPlaceKey);
                myPlaces.remove(index);
                recreateKeyIndexMapping();
            }
        }

        @Override
        public void onChildMoved(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

        }
    };

    private static class SingletonHolder{
        public static final MyPlacesData instance = new MyPlacesData();
    }

    public static MyPlacesData getInstance()
    {
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces() {
        return myPlaces;
    }

    public void addNewPlace(MyPlace place)
    {
        String key = database.push().getKey();
        myPlaces.add(place);
        myPlaceKeyIndexMapping.put(key, myPlaces.size() - 1);
        database.child(FIREBASE_CHILD).child(key).setValue(place);
        place.key = key;
    }

    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        database.child(FIREBASE_CHILD).child(myPlaces.get(index).key).removeValue();
        myPlaces.remove(index);
        recreateKeyIndexMapping();
    }

    public void updatePlace(int index, String nme, String desc, String lon, String lat)
    {
        MyPlace myPlace = myPlaces.get(index);
        myPlace.name = nme;
        myPlace.description = desc;
        myPlace.longitude = lon;
        myPlace.latitude = lat;

        database.child(FIREBASE_CHILD).child(myPlace.key).setValue(myPlace);
    }

    private void recreateKeyIndexMapping()
    {
        myPlaceKeyIndexMapping.clear();
        for(int i = 0 ; i < myPlaces.size() ; i++)
            myPlaceKeyIndexMapping.put(myPlaces.get(i).key, i);
    }

    public interface ListUpdatedEventListener{
        void onListUpdated();
    }
}
