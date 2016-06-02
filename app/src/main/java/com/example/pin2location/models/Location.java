package com.example.pin2location.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by meliodas on 13/05/16.
 */
public class Location implements Parcelable {
    private Geometry geometry;
    private GProperties properties;
    private String type;

    public Location() { }

    public Location(Geometry geometry, GProperties properties, String type) {
        this.geometry = geometry;
        this.properties = properties;
        this.type = type;
    }

    public Location(Parcel source) {
        this.geometry = (Geometry) source.readSerializable();
        this.properties = (GProperties) source.readSerializable();
        this.type = source.readString();
    }

    public GProperties getProperties() {
        return properties;
    }

    public void setProperties(GProperties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        applyDefaultValues();

        dest.writeSerializable(geometry);
        dest.writeSerializable(properties);
        dest.writeString(type);
    }

    private void applyDefaultValues() {
        if (geometry == null)
            geometry = new Geometry();

        if (properties == null)
            properties = new GProperties();

        if (type == null)
            type = "";
    }

    public static Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}

