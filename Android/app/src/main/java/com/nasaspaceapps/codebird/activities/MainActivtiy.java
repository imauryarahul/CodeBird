package com.nasaspaceapps.codebird.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nasaspaceapps.codebird.R;
import com.nasaspaceapps.codebird.utils.SatusBar;
import com.nasaspaceapps.codebird.utils.UserRegistration;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.functions.Action1;

public class MainActivtiy extends AppCompatActivity {

    CardView cardView1, cardView2, cardView3;
    AlertDialog dialog;

    private static final int CAMERA_REQUEST = 1888;
    String timestamp;
    File file;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SatusBar.setTransparent(this);
        setContentView(R.layout.activity_main);
        dialog = new SpotsDialog(MainActivtiy.this);


        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        cardView1 = (CardView) findViewById(R.id.leaderBoard);

        cardView2 = (CardView) findViewById(R.id.new_sight);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LeaderBoardsActivity.class));

            }
        });

        cardView3 = (CardView) findViewById(R.id.your_sights);

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserSightsActivity.class));
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomeButtonClick();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ImageView profile = (ImageView) findViewById(R.id.profile_image);

        Picasso.with(getApplicationContext()).load(Prefs.getString("user_image", "")).into(profile);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView imageView = (ImageView) findViewById(R.id.profile_back);
        final String imageUri = "https://s3-us-west-2.amazonaws.com/motivationalsmall/back+(1).png";
        Picasso.with(getApplicationContext())
                .load(imageUri)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Prefs.putBoolean("loaded", true);
                    }

                    @Override
                    public void onError() {
                        Log.v("Picasso", "Could not fetch image");
                    }
                });


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void onSomeButtonClick() {
        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else doLocationAccessRelatedJob();

    }

    private void doLocationAccessRelatedJob() {

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProvider.getLastKnownLocation()
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        Prefs.putString("latitude", String.valueOf(location.getLatitude()));
                        Prefs.putString("longitude", String.valueOf(location.getLongitude()));
                        Log.e("Location", location.getLatitude() + "\n" + location.getLongitude());


                        activeTakePhoto();

                    }
                });

    }


    public void uploadMultipart(File fi) {
        //getting name for the image

        //getting the actual path of the image
        String path = fi.getPath();

        //Uploading code
        try {
            String uploadId = timestamp;
            Log.e("Path", path);

            //Creating a multi part request
            new MultipartUploadRequest(getApplicationContext(), uploadId, "http://35.165.216.139:8080/upload_simage")
                    .addFileToUpload(path, "bn")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2).setDelegate(new UploadStatusDelegate() {
                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {
                    // your code here
                    dialog.show();
                    dialog.setMessage("Please Wait");

                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                    // your code here
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                    // your code here
                    // if you have mapped your server response to a POJO, you can easily get it:
                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                    Log.e("Uploaded", serverResponse.getBodyAsString());


                    try {
                        Prefs.putInt("count", Prefs.getInt("count", 0) + 1);
                        int score = Prefs.getInt("score", 0);
                        score = score + 3;
                        Prefs.putInt("score", score);
                        JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                        UserRegistration userRegistration = new UserRegistration(getApplicationContext());
                        userRegistration.sendSightData("https://s3-us-west-2.amazonaws.com" + jsonObject.getString("path"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                    showLocationDialog();


                }

                @Override
                public void onCancelled(Context context, UploadInfo uploadInfo) {
                    // your code here
                }
            }).startUpload();//Starting the upload

        } catch (Exception exc) {
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void activeTakePhoto() {
        Calendar cal = Calendar.getInstance();
        timestamp = cal.getTimeInMillis() + "";

        file = new File(Environment.getExternalStorageDirectory(), (timestamp + ".jpg"));
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(file));
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            uploadMultipart(file);

        }

    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                doLocationAccessRelatedJob();
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
                startInstalledAppDetailsActivity();
            }
        }
    }


    private void startInstalledAppDetailsActivity() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivtiy.this);
        builder.setTitle("Sight Status");
        builder.setMessage("You have successfully submitted.");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


}
