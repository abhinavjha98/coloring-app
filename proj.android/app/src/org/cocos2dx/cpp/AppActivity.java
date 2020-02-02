/****************************************************************************
 Copyright (c) 2015-2016 Chukong Technologies Inc.
 Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.cpp;

import android.os.Bundle;
import android.view.View;
import org.cocos2dx.lib.Cocos2dxActivity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;
import android.support.v4.content.FileProvider;
import android.support.annotation.Keep;

import com.yourcompany.mycoloringbook.BuildConfig; //add your package name after write .BuildConfig
import com.yourcompany.mycoloringbook.R;           //add your package name after write .R

public class AppActivity extends Cocos2dxActivity {

    private static AdManager admanager =  null;
    private static AppActivity _this;
    public static boolean admobfullpageavailable =  false;


    private static final int PERMISSION_REQUEST_CODE = 9001;
    @Keep
    public static void askForPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(Cocos2dxHelper.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Keep
    public static boolean hasPermission() {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = (ContextCompat.checkSelfPermission(Cocos2dxHelper.getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
        askForPermission();
        if (!isTaskRoot()) {
            return;
        }

        admanager =  new AdManager(this);
        _this = this;


    }

    public static void bannerEnabled(){

        _this. runOnUiThread(new Runnable() {
            @Override
            public void run() {

                admanager.BannerDisplay();
            }
        });

    }

    public static  void HideBanner(){

        _this. runOnUiThread(new Runnable() {
            @Override
            public void run() {
                admanager.BannerHide();
            }
        });

    }


    public static boolean isInterstitialAvailable(){

        return admobfullpageavailable;
    }

    public static void showInterstitial(){

        _this. runOnUiThread(new Runnable() {
            @Override
            public void run() {
                admanager.ShowInterstitial();
            }
        });

    }

    public static void OpenMoreGame()
    {
        String url="";
        Intent storeintent=null;


        String moreURL = getContext().getApplicationContext().getString(R.string.more_game_url);
        url = moreURL;
        storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        _this.startActivity(storeintent);

    }
    public static void openRateGame()
    {
        String url="";
        Intent storeintent=null;

        String rateURL = getContext().getApplicationContext().getString(R.string.rate_game_url);
        url = rateURL;
        storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        _this.startActivity(storeintent);
    }


    public void SaveToGallery(String imgname) {

        final String Img = imgname;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.i("Devid : ImageName : ", Img);
                ContextWrapper c = new ContextWrapper(getContext());
                String path = c.getFilesDir().getPath() + File.separator + Img;
                Log.i("Devid : Path :", path);

                File imgFile = new File(path);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                ArrayList<Uri> uris = new ArrayList<Uri>();
                uris.add(Uri.parse(path));

                OutputStream output;

                File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Drawing");
                directory.mkdirs();
                File file = new File(directory + File.separator + Img);

                try {
                    output = new FileOutputStream(file);
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                _this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(_this, "Image Saved Sucessfully! ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }
    public static AppActivity getInstance() {
        Log.i("Call", "getInstance");
        return (AppActivity) _this;
    }
    public void ShareDialog(String Imgname) {

        ContextWrapper c = new ContextWrapper(getContext());
        String path = c.getFilesDir().getPath() + "/" + Imgname;

        Log.i(" Devid ", path);

        File imgFile = new File(path);

        try {
            Intent intent = new Intent();

            intent.setType("text/plain");
            String rateURL = getContext().getApplicationContext().getString(R.string.rate_game_url);
            intent.putExtra(Intent.EXTRA_TEXT, "Try this amazing app \n"+rateURL);

            //Uri pngURi = FileProvider.getUriForFile(_this, BuildConfig.APPLICATION_ID + ".provider",imgFile);
            //String temp = _this.getApplicationContext().getString(R.string.app_name);
            Uri pngURi = FileProvider.getUriForFile(getContext().getApplicationContext(), "com.as.MyColoringBook.fileprovider", imgFile);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, pngURi);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContext().startActivity(Intent.createChooser(intent, "Share Using"));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void BackButtonClicked(){

        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(_this,
                        R.style.MyAlertDialogStyle);
                builder.setTitle(_this.getResources().getString(R.string.app_name));
                builder.setCancelable(false);
                builder.setMessage("Do you want to EXIT");
                builder  .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _this.finish();
                    }

                })
                        .setNegativeButton("No", null);

                builder.show();
            }
        });

    }





}
