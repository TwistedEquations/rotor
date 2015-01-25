package com.twistedequations.rotor;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MetaDataClient {

    private AtomicBoolean isLoading = new AtomicBoolean();

    //Controls when a result is pending for rotor to pick up
    private AtomicBoolean isPending = new AtomicBoolean();

    private MediaMetadataCompat cachedMetaData;
    /**
     * Requests new metadata to be displayed in the notification and the lockscreen etc
     * <br>
     * If you need to async some work such as downloading bitmap or reading from a
     * database call updateNotification to update the notifications.
     *
     * @return A default metadata object.
     */
    protected abstract MediaMetadataCompat onRequestMetaData(Position position);

    /**
     * Call this to update the metadata shown after information has been
     * @param metaData metadata to update
     */
    public final void updateMetaData(MediaMetadataCompat metaData) {
        isLoading.set(false);
        if(metaData == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }

        compareMetaData(metaData);
    }

    /**
     * Called by subclasses to notify that filling the metadata is an async operation
     */
    public final void unlatch() {
        isLoading.set(true);
    }

    /**
     * @return true if the Stack is loading new metadata
     */
    public final boolean isAync() {
        return isLoading.get();
    }

    public final boolean isPending() {
        return isPending.get();
    }

    /**
     * Gets the meta data and resets pending flag
     */
    public final MediaMetadataCompat getMetaData() {
        isPending.set(false);
        return cachedMetaData;
    }

    public final void requestMetaData(Position position) {
        if(isPending()) {
            return;
        }
        MediaMetadataCompat metaData = onRequestMetaData(position);
        if(metaData != null) {
            compareMetaData(metaData);
        }
    }

    //Sets the new metadata and the pending flag if the metadata is different
    private void compareMetaData(MediaMetadataCompat metaData) {
        if(cachedMetaData == null || cachedMetaData.equals(metaData)) {
            this.cachedMetaData = metaData;
            isPending.set(true);
        }

    }
}
