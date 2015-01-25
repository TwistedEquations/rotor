/**
 * Copyright (C) 03/01/2015 Patrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twistedequations.rotor.toolbox;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.KitkatMediaButtonReceiver;
import com.twistedequations.rotor.MediaControl;
import com.twistedequations.rotor.MediaDescriptionCompat;
import com.twistedequations.rotor.MediaMetadataCompat;
import com.twistedequations.rotor.R;
import com.twistedequations.rotor.Rotor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultRemoteControl implements MediaControl {

    private final Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private Control control;

    public DefaultRemoteControl(Context context) {
        this.context = context.getApplicationContext();
        notificationManagerCompat = NotificationManagerCompat.from(context);
        control = getRemoteControlIMPL();
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public void create() {
        control.create();
    }

    @Override
    public void destroy() {
        control.destroy();
    }

    @Override
    public void updateState(int state) {
        switch (state) {
            case Rotor.STATE_WAITING:
            case Rotor.STATE_ERROR:
              control.dimiss();
                return;
        }
        control.updateAction(getActionsForState(state));
    }

    protected List<Action> getActionsForState(int state) {
        List<Action> actionList = new ArrayList<>();
        switch (state) {
            case Rotor.STATE_PLAYING:
                actionList.add(Action.PAUSE);
                break;

            case Rotor.STATE_PAUSED:
                actionList.add(Action.PLAY);
                break;
        }
        actionList.add(Action.STOP);
        return actionList;
    }

    @Override
    public void onUpdateMetaData(MediaMetadataCompat metaData) {
        control.onUpdateMetaData(metaData);
    }

    protected String getDefaultName() {
        return getContext().getApplicationInfo().loadLabel(getContext().getPackageManager()).toString();
    }

    protected PendingIntent getNotificationContentIntent() {
        return null;
    }

    private Control getRemoteControlIMPL() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new LControlIMPL(getContext());
        }
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return new KKControlIMPL(getContext());
        }
        return new DefaultControlImpl(getContext());
    }

    private static abstract class Control {

        private final Context context;

        protected Control(Context context) {
            this.context = context.getApplicationContext();
        }

        protected Context getContext() {
            return context;
        }

        public abstract void create();
        public abstract void dimiss();
        public abstract void destroy();
        public abstract void updateAction(Collection<Action> actions);
        public abstract void onUpdateMetaData(MediaMetadataCompat metaData);
    }

    private class DefaultControlImpl extends Control {

        private List<Action> mActions = new ArrayList<>(10);
        private MediaMetadataCompat mMedaData;

        protected DefaultControlImpl(Context context) {
            super(context);
        }

        @Override
        public void create() {
        }

        @Override
        public void dimiss() {
            notificationManagerCompat.cancel(R.id.rotor_notification_id);
            mActions.clear();
            mMedaData = null;
        }

        @Override
        public void destroy() {
            notificationManagerCompat.cancel(R.id.rotor_notification_id);
            mActions.clear();
            mMedaData = null;
        }

        @Override
        public void updateAction(Collection<Action> actions) {
            if(!mActions.containsAll(actions)) {
                //actions don't match update the notification
                mActions.clear();
                mActions.addAll(actions);
                updateNotification(mActions, mMedaData);
            }
        }

        @Override
        public void onUpdateMetaData(MediaMetadataCompat metaData) {
            if(mMedaData == null) {
                //No old meta data, update
                mMedaData = metaData;
                updateNotification(mActions, metaData);
            }
            else if(!mMedaData.equals(metaData)) {
                mMedaData = metaData;
                updateNotification(mActions, metaData);
            }
        }

        protected void updateNotification(List<Action> actions, MediaMetadataCompat metaData) {
            String packageName = getContext().getPackageName();
            RemoteViews remoteSmallViews = new RemoteViews(packageName, R.layout.notif_playing_small);
            //add the first 2 action views
            int smallActionCount = Math.min(2, actions.size());
            for (int i = 0; i < smallActionCount; i++) {

                RemoteViews actionView = new RemoteViews(packageName, R.layout.notif_action_small);
                Action action = mActions.get(0);
                if(action.hasIcon()) {
                    actionView.setImageViewResource(R.id.notif_action, action.getIcon());
                }

                //click intent for the buttons
                remoteSmallViews.removeAllViews(R.id.action_layout);
                actionView.setOnClickPendingIntent(R.id.notif_action, getIntentForAction(action, i));
                remoteSmallViews.addView(R.id.action_layout, actionView);
            }

            RemoteViews remoteBigViews = null;

            if(metaData == null) {
                //load a default notification
                String defaultName = getDefaultName();
                remoteSmallViews.setTextViewText(R.id.title, defaultName);
            }
            else {
                MediaDescriptionCompat mediaDescriptionCompat = metaData.getDescription();
                remoteSmallViews.setTextViewText(R.id.title, mediaDescriptionCompat.title);
                remoteSmallViews.setTextViewText(R.id.song_name, mediaDescriptionCompat.subtitle);
                if(mediaDescriptionCompat.icon != null) {
                    remoteSmallViews.setViewVisibility(R.id.artwork, View.VISIBLE);
                    remoteSmallViews.setImageViewBitmap(R.id.artwork, mediaDescriptionCompat.icon);
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //create the big view notification

                    remoteBigViews = new RemoteViews(packageName, R.layout.notif_playing_large);
                    for (int i = 0; i < mActions.size(); i++) {
                        RemoteViews actionView = new RemoteViews(packageName, R.layout.notif_action_large);
                        Action action = mActions.get(0);
                        if(action.hasIcon()) {
                            actionView.setImageViewResource(R.id.notif_action_large, action.getIcon());
                        }

                        //click intent for the buttons
                        remoteSmallViews.removeAllViews(R.id.action_layout);
                        actionView.setOnClickPendingIntent(R.id.notif_action_large, getIntentForAction(action, i));
                        remoteBigViews.addView(R.id.controls, actionView);
                    }
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
            builder.setOngoing(true);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setContent(remoteSmallViews);
            builder.setSmallIcon(R.drawable.rotor_action_play);

            PendingIntent pendingIntent = getNotificationContentIntent();
            if(pendingIntent != null) {
                builder.setContentIntent(pendingIntent);
            }

            Notification notification = builder.build();
            notification.contentView = remoteSmallViews;
            if(remoteBigViews != null) {
                notification.bigContentView = remoteBigViews;
            }

            notificationManagerCompat.notify(R.id.rotor_notification_id, notification);
        }

        protected PendingIntent getIntentForAction(Action action, int index) {
            Intent intent = new Intent(Rotor.INTENT_ACTION);
            intent.putExtra(Rotor.INTENT_KEY, action);
            return PendingIntent.getBroadcast(getContext(), R.id.rotor_action_id + index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private class KKControlIMPL extends DefaultControlImpl {

        private AudioManager audioManager;
        private RemoteControlClient mRemoteControlClient;
        private ComponentName mEventReceiver;

        protected KKControlIMPL(Context context) {
            super(context);
            audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public void create() {
            super.create();
            mEventReceiver = new ComponentName(getContext().getPackageName(), KitkatMediaButtonReceiver.class.getName());
            audioManager.registerMediaButtonEventReceiver(mEventReceiver);

            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            mediaButtonIntent.setComponent(mEventReceiver);
            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getContext(), 0, mediaButtonIntent, 0);
            mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
            audioManager.registerRemoteControlClient(mRemoteControlClient);
        }

        @Override
        public void destroy() {
            super.destroy();
            if(mRemoteControlClient != null) {
                audioManager.unregisterRemoteControlClient(mRemoteControlClient);
                audioManager.unregisterMediaButtonEventReceiver(mEventReceiver);
            }
        }

        @Override
        public void updateAction(Collection<Action> actions) {
            super.updateAction(actions);
            
            int flagBitmask = 0;
            for(Action action : actions) {
                switch (action.getAction()) {
                    case Rotor.ACTION_PLAY:
                        flagBitmask = flagBitmask | RemoteControlClient.FLAG_KEY_MEDIA_PLAY;
                        break;
                    case Rotor.ACTION_PAUSE:
                        flagBitmask = flagBitmask | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE;
                        break;
                    case Rotor.ACTION_NEXT:
                        flagBitmask = flagBitmask | RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
                        break;
                    case Rotor.ACTION_PREV:
                        flagBitmask = flagBitmask | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
                        break;
                    case Rotor.ACTION_STOP:
                        flagBitmask = flagBitmask | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
                        break;
                }
            }
            mRemoteControlClient.setTransportControlFlags(flagBitmask);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class LControlIMPL extends DefaultControlImpl {

        MediaSession mMediaSession;

        protected LControlIMPL(Context context) {
            super(context);
        }

        @Override
        public void create() {
            mMediaSession = new MediaSession(getContext(), "ROTOR SESSION");
            mMediaSession.setActive(true);
            mMediaSession.setCallback(callback);
            mMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            super.create();
        }

        @Override
        public void destroy() {
            super.destroy();
            mMediaSession.setActive(false);
            mMediaSession.release();
            mMediaSession = null;
        }

        @Override
        protected void updateNotification(List<Action> actions, MediaMetadataCompat metaData) {


            //not calling super as we use a different notification
            Notification.Builder builder = new Notification.Builder(getContext());
            builder.setContentIntent(getNotificationContentIntent());
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.rotor_action_play);

            for (int i = 0; i < actions.size(); i++) {
                Action action = actions.get(i);
                Notification.Action notifAction = new Notification.Action.Builder(action.getIcon(), action.getTitle(), getIntentForAction(action, i)).build();
                builder.addAction(notifAction);
            }

            Notification.MediaStyle style = new Notification.MediaStyle();
            int smallActionCount = Math.min(2, actions.size());
            int[] number = new int[smallActionCount];

            for (int i = 0; i < number.length; i++) {
                number[i] = i;
            }

            style.setShowActionsInCompactView(number);
            style.setMediaSession(mMediaSession.getSessionToken());
            builder.setStyle(style);

            if(metaData != null) {
                MediaDescriptionCompat mediaDescriptionCompat = metaData.getDescription();
                if(mediaDescriptionCompat.icon != null) {
                    builder.setLargeIcon(mediaDescriptionCompat.icon);
                }

                builder.setContentTitle(mediaDescriptionCompat.title);
                builder.setContentText(mediaDescriptionCompat.subtitle);
            }
            notificationManagerCompat.notify(R.id.rotor_notification_id, builder.build());
        }

        private  MediaSession.Callback callback = new MediaSession.Callback() {
            @Override
            public void onCustomAction(String action, Bundle extras) {
                super.onCustomAction(action, extras);
            }
        };
    }
}
