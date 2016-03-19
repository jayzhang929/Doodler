package com.example.jayzhang.doodler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DoodleView doodleView;
    int curPaintColor = 0;
    int curStrokeWidth = 0;
    int curOpacity = 0;

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
        // ********** Bug:paintDoodle properties doesn't get succeed from previous props before clearing **********
        layout.addView(doodleView);
    }
}
