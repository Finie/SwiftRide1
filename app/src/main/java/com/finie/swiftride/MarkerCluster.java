package com.finie.swiftride;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerCluster implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int Icon;
    private String User;

    public MarkerCluster(LatLng position, String title, String snippet, int icon, String user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        Icon = icon;
        User = user;
    }

    public MarkerCluster() {
    }


    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}
