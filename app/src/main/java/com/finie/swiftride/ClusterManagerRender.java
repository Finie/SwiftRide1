package com.finie.swiftride;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRender extends DefaultClusterRenderer<MarkerCluster>{


 private final IconGenerator iconGenerator;
 private ImageView imageView;
 private  int width;
 private   int height;

    public ClusterManagerRender(Context context, GoogleMap map, ClusterManager<MarkerCluster> clusterManager) {
        super(context, map, clusterManager);


        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        width = (int) context.getResources().getDimension(R.dimen.cluster_width);
        height = (int) context.getResources().getDimension(R.dimen.cluster_height);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width,height));
        int padding = (int) context.getResources().getDimension(R.dimen.marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIcon());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<MarkerCluster> cluster) {

        return false;
    }





}
