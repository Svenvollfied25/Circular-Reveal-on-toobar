package com.example.nikhil_kamath.imageviewwithcircularreveal;


import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by nikhil-kamath on 26/11/16.
 */
public class MainActivity extends AppCompatActivity {
    private int RESULT_LOAD_IMAGE = 00023;
    private ImageView im;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout myView = (LinearLayout) findViewById(R.id.revealView);
        Button b1  = (Button) findViewById(R.id.buttonClick);
        im = (ImageView) findViewById(R.id.imageView);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            im.setImageBitmap(getScaledBitmap(picturePath,200,200));

            final LinearLayout myView = (LinearLayout) findViewById(R.id.revealView);
            Palette.from(getScaledBitmap(picturePath,200,200)).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    if (vibrantSwatch != null) {
//                        outerLayout.setBackgroundColor(vibrantSwatch.getRgb());
//                        titleText.setTextColor(vibrantSwatch.getTitleTextColor());
//                        bodyText.setTextColor(vibrantSwatch.getBodyTextColor());
                        myView.setBackgroundColor(vibrantSwatch.getRgb());
                        int cx = (myView.getLeft() + myView.getRight()) / 2;
                        int cy = (myView.getTop() + myView.getBottom()) / 2;

                        // get the final radius for the clipping circle
                        int dx = Math.max(cx, myView.getWidth() - cx);
                        int dy = Math.max(cy, myView.getHeight() - cy);
                        float finalRadius = (float) Math.hypot(dx, dy);

                        // Android native animator
                        Animator animator =
                                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());

                        animator.setDuration(1500);
                        animator.start();

                    }
                }
            });
        }
    }
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
