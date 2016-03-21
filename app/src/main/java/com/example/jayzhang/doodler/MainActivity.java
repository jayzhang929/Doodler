package com.example.jayzhang.doodler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public DoodleView doodleView;
    public int curPaintColor = 0;
    public int curStrokeWidth = 0;
    public int curOpacity = 0;
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        doodleView = (DoodleView) findViewById(R.id.doodleView);

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openProperty(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.alert_dialog, (ViewGroup) findViewById(R.id.dialogLayout));
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);

        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
        seekBar.setProgress(curStrokeWidth - 1);

        final SeekBar opacitySeekBar = (SeekBar) layout.findViewById(R.id.opacity_seekBar);
        opacitySeekBar.setProgress(curOpacity / 10);

        final RadioGroup colorRadioGroup = (RadioGroup) layout.findViewById(R.id.radioGroup);
        checkCurrentPaintColor(colorRadioGroup, curPaintColor);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = colorRadioGroup.getCheckedRadioButtonId();
                RadioButton checkedButt = (RadioButton) layout.findViewById(selectedId);
                curPaintColor = checkedButt.getCurrentTextColor();
                doodleView.paintDoodle.setColor(curPaintColor);

                curStrokeWidth = seekBar.getProgress() + 1;
                doodleView.paintDoodle.setStrokeWidth(curStrokeWidth);

                curOpacity = opacitySeekBar.getProgress() * 10;
                doodleView.paintDoodle.setAlpha(curOpacity);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkCurrentPaintColor(RadioGroup colorRadioGroup, int currentColor) {
        // Log.d("current paint color: ", String.valueOf(currentColor));
        if (currentColor == getResources().getColor(R.color.black))
            colorRadioGroup.check(R.id.black);
        else if (currentColor == getResources().getColor(R.color.red))
            colorRadioGroup.check(R.id.red);
        else if (currentColor == getResources().getColor(R.color.green))
            colorRadioGroup.check(R.id.green);
        else if (currentColor == getResources().getColor(R.color.blue))
            colorRadioGroup.check(R.id.blue);
        else if (currentColor == getResources().getColor(R.color.yellow))
            colorRadioGroup.check(R.id.yellow);
        else if (currentColor == getResources().getColor(R.color.cyan))
            colorRadioGroup.check(R.id.cyan);
        else if (currentColor == getResources().getColor(R.color.purple))
            colorRadioGroup.check(R.id.purple);
        else if (currentColor == getResources().getColor(R.color.grey))
            colorRadioGroup.check(R.id.grey);
    }

    public void clearDoodle(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);

        layout.removeView(doodleView);
        doodleView = new DoodleView(MainActivity.this);
        doodleView.setId(R.id.doodleView);
        doodleView.paintDoodle.setColor(curPaintColor);
        doodleView.paintDoodle.setStrokeWidth(curStrokeWidth);
        doodleView.paintDoodle.setAlpha(curOpacity);

        /*
        Log.d("MainAcitity: ", "clearDoodle method");
        Log.d("strokeWidth: ", String.valueOf(doodleView.paintDoodle.getStrokeWidth()));
        Log.d("strokeOpacity: ", String.valueOf(doodleView.paintDoodle.getAlpha()));
        Log.d("currentColor: ", String.valueOf(doodleView.paintDoodle.getColor()));
        */

        // ********** Bug:paintDoodle properties doesn't get succeed from previous props before clearing **********
        layout.addView(doodleView);

    }

    public void saveImage(MenuItem item) {
        // if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //        != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap toBeSaved = getBitmapFromView(doodleView);
                    MediaStore.Images.Media.insertImage(getContentResolver(), toBeSaved, "", "");
                    Toast.makeText(this, "Drawing Saved!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void uploadPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                doodleView.bitmap = Bitmap.createScaledBitmap(bitmap, doodleView.getWidth(), doodleView.getHeight(), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /*
    * Open source code from http://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
    * */
    private static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

}
