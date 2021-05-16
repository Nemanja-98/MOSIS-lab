package elfakrs.mosis.vitaminc.myplaces.presenters;
import java.util.ArrayList;

import elfakrs.mosis.vitaminc.myplaces.models.MyPlace;

public class MyPlacesData {
    private ArrayList<MyPlace> myPlaces;

    private MyPlacesData()
    {
        myPlaces = new ArrayList<MyPlace>();
//        myPlaces.add(new MyPlace("Park Sv. Save"));
//        myPlaces.add(new MyPlace("Cair"));
//        myPlaces.add(new MyPlace("Bubanj"));
//        myPlaces.add(new MyPlace("Gradska Basta, Nis"));
    }

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
        myPlaces.add(place);
    }

    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        myPlaces.remove(index);
    }
}
