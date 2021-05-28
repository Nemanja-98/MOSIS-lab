package elfakrs.mosis.vitaminc.myplaces.models;

import androidx.annotation.NonNull;

public class MyPlace {
    private String name;
    private String description;
    private String longitude;
    private String latitude;
    private int ID;

    public MyPlace(String name, String desc)
    {
        this.name = name;
        this.description = desc;
    }

    public MyPlace(String name)
    {
        this(name, "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
