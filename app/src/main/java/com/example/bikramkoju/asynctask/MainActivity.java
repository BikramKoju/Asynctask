package com.example.bikramkoju.asynctask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    Button btnshowprogress;
    ImageView my_image;

    //progressDialog
    private ProgressDialog pDialog;

   //for type fo progress dialog, 0-for horizontal progress dialog
    static final int progress_bar_type=0;
//url for downloading image
    String file_url="http://api.androidhive.info/progressdialog/hive.jpg";

    //runtime permission
    private static final int REQUEST_CODE_PERMISSION=3;
    String [] mPermission={android.Manifest.permission.INTERNET, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
    boolean permissioncheck=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //api 23+ runtime permission
        try{
            if(ActivityCompat.checkSelfPermission(this, mPermission[0])
                    != MockPackageManager.PERMISSION_GRANTED||
            ActivityCompat.checkSelfPermission(this, mPermission[1])
                    !=MockPackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(this, mPermission[2])
                    !=MockPackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, mPermission, REQUEST_CODE_PERMISSION);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        btnshowprogress=(Button)findViewById(R.id.btnProgressBar);
        my_image=(ImageView)findViewById(R.id.my_image);

        btnshowprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileUrl().execute(file_url);

            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case progress_bar_type:
                pDialog=new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Downloading file....please wait...");
                //no fixed time duration for dialog, so false
                pDialog.setIndeterminate(false);
                //maximum value goes upto 100%
                pDialog.setMax(100);
                //horizontal progressbar
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //can be cancelled at any time
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;

        }

    }

    private class DownloadFileUrl extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);

        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                //getting file length
                int lengthoffile = connection.getContentLength();

                //input stream to read file--with 8k size buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //output stream for writing file to device
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    //publishing progress
                    publishProgress("" + (int) (total * 100) / lengthoffile);
                    output.write(data, 0, count);


                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //setting progress in progressdialog
            pDialog.setProgressStyle(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissDialog(progress_bar_type);
            //path for extracting image to imageview
            String imagePath= Environment.getExternalStorageDirectory().toString()+"/downloadedfile.jpg";
            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode== REQUEST_CODE_PERMISSION){
            if (grantResults.length==3 &&
                grantResults[0]==MockPackageManager.PERMISSION_GRANTED
                        && grantResults[1]==MockPackageManager.PERMISSION_GRANTED
                        && grantResults[2]==MockPackageManager.PERMISSION_GRANTED){
                permissioncheck = true;
            }
        }else{
            permissioncheck=false;
        }
    }
}
