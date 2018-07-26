package com.greece.nasiakouts.babysitterfinder.Services;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FirebaseRegistrationService {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersTokensReference;

    public FirebaseRegistrationService() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsersTokensReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_REG_TOKENS);
    }

    public void pushRegistrationToken(String token) {
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        /* Update User Registration Token */
        mUsersTokensReference
                .child(currentUser.getUid())
                .setValue(token);
    }

    public void registerToPushService(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager notificationManager = (NotificationManager)
                activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        String channelId = activity.getString(R.string.default_notification_channel_id);
        String channelName = activity.getString(R.string.default_notification_channel_name);

        notificationManager.createNotificationChannel(
                new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT));

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(activity, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        pushRegistrationToken(instanceIdResult.getToken());
                    }
                });
    }

    public void sendPushToUser(final String userId, final String title, final String message) {
        mUsersTokensReference
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String token = dataSnapshot.getValue(String.class);
                        new SendPushNotificationAsyncTask().execute(token, title, message);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Param 0 : User Token
     * Param 1 : Title
     * Param 2 : Message
     */
    private static class SendPushNotificationAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final String... strings) {
            if (strings.length != 3) return false;

            OkHttpClient httpClient = new OkHttpClient();

            JSONObject dataJson = new JSONObject();
            JSONObject messageJson = new JSONObject();
            try {
                dataJson.put("title", strings[1]);
                dataJson.put("body", strings[2]);

                messageJson.put("notification", dataJson);
                messageJson.put("to", strings[0]);

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        messageJson.toString());

                Request request = new Request.Builder()
                        .header("Authorization", "key = " + Constants.LEGACY_SERVER_KEY)
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .build();

                httpClient.newCall(request).execute();
            } catch (Exception ignored) {
                return false;
            }
            return true;
        }
    }
}
