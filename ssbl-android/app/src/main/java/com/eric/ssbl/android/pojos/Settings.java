package com.eric.ssbl.android.pojos;

import java.io.Serializable;

public class Settings implements Serializable {

    private boolean _alert;
    private boolean _locationPrivate;
    private int _mapRadius;

    public Settings(boolean alert, boolean locationPrivate, int mapRadius) {
        _alert = alert;
        _locationPrivate = locationPrivate;
        _mapRadius = mapRadius;
    }

    public void setAlert(boolean alert) {
        _alert = alert;
    }

    public boolean getAlert() {
        return _alert;
    }

    public void setLocationPrivate(boolean lp) {
        _locationPrivate = lp;
    }

    public boolean getLocationPrivate() {
        return _locationPrivate;
    }

    public void setMapRadiusIndex(int mapRadius) {
        _mapRadius = mapRadius;
    }

    public int getMapRadiusIndex() {
        return _mapRadius;
    }

}
