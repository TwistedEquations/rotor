package com.android.rotor;

import java.util.Collection;

public interface Playlist {

    /**
     * Blocking call, takes the current track off the player
     * @return
     */
    public String getCurrent();

    public void add(String source);

    public void addAll(Collection<String> sources);

    public void clear();

    public boolean next();

    public boolean prev();

    public boolean moveTo(int pos);

    public int size();

    public boolean empty();

    public int getPosition();

}
