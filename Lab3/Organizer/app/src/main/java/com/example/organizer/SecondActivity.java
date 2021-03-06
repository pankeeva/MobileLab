package com.example.organizer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

public class SecondActivity extends AppCompatActivity {

    ImageView imageBack;
    Button buttonSave;
    TextView title, textArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imageBack = (ImageView) findViewById(R.id.imageView);
        buttonSave = (Button) findViewById(R.id.button);
        title = (TextView) findViewById(R.id.textView2);
        textArea = (TextView) findViewById(R.id.textView3);

        View.OnClickListener onClickBack = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        };

        imageBack.setOnClickListener(onClickBack);

        View.OnClickListener onClickSave = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(textArea.length() == 0 || title.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Attention!")
                            .setMessage("You could not save note without text or title!")
                            .setCancelable(false)
                            .setNegativeButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    String titleStr = title.getText().toString();
                    String textStr = textArea.getText().toString();

                    SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TITLE, titleStr);
                    contentValues.put(DBHelper.KEY_TEXT, textStr);

                    database.insert(DBHelper.TABLE_NOTES, null, contentValues);

                    finish();
                }
            }
        };

        buttonSave.setOnClickListener(onClickSave);

        //MainActivity.dbHelper.close();
    }
}
