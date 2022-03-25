package com.kinetise.data.systemdisplay;

public interface IRebuildableView {
    /**
     * This method asks view to rebuild all child views based on the descriptors hierarchy.
     * This will be usually achieved by updating adapter or, rarely, by actual views recreating.
     */
    void rebuildView();
}
