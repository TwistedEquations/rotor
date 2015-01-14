package com.twistedequations.rotor;

public abstract class MetaDataStack {

    /**
     * Requests new metadata to be displayed in the notification and the lockscreen etc
     * <br>
     * If you need to async some work such as downloading bitmap or reading from a
     * database call updateNotification to update the notifications.
     *
     * @return A default metadata object.
     */
    public abstract MetaData requestMetaData();

    /**
     * Call this to update the metadata shown
     * @param metaData metadata to update
     */
    public abstract void updateMetaData(MetaData metaData);
}
