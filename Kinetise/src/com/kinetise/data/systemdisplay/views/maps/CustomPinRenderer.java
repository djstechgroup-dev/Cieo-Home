package com.kinetise.data.systemdisplay.views.maps;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashMap;
import java.util.Map;

public class CustomPinRenderer extends DefaultClusterRenderer<PinInfo> {
    private RendererState mRendererState;
    private Map<String, BitmapDescriptor> mPinsMap;

    public CustomPinRenderer(Context context, GoogleMap map, ClusterManager<PinInfo> clusterManager, RendererState rendererState) {
        super(context, map, clusterManager);
        mRendererState = rendererState;
        initPinMaps();
    }

    private void initPinMaps() {
        mPinsMap = new HashMap<>();
    }

    @Override
    protected void onBeforeClusterItemRendered(PinInfo item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if (item != null && item.getPinBitmap() != null) {
            markerOptions.icon(getBitmapDescriptor(item.getPinBitmap()));
        }
    }

    private BitmapDescriptor getBitmapDescriptor(Bitmap bitmap) {
        BitmapDescriptor descriptor = mPinsMap.get(bitmap.toString());
        if (descriptor != null) {
            return descriptor;
        } else {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            mPinsMap.put(bitmap.toString(), bitmapDescriptor);
            return bitmapDescriptor;
        }
    }


    @Override
    protected void onClusterItemRendered(PinInfo clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        mRendererState.onMarkerIsReady();
    }

    @Override
    protected void onClusterRendered(Cluster<PinInfo> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
        mRendererState.onMarkerIsReady();
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<PinInfo> cluster) {
        return cluster.getSize() > 1;
    }


    public interface RendererState {
        void onMarkerIsReady();
    }

}
