package com.example.organizer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.text.Editable;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

public class MainActivity extends AppCompatActivity {

    Button buttonPlus, buttonDelete, buttonEdit;
    EditText editText;
    ImageView imageSearch, imageClear;
    LinearLayout layout, layoutSearch;
    SQLiteDatabase database;

    public static DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buttonPlus = (Button)findViewById(R.id.button);
        buttonEdit = (Button)findViewById(R.id.button2);
        buttonDelete = (Button)findViewById(R.id.button1);
        editText = (EditText)findViewById(R.id.textView2);
        imageSearch = (ImageView) findViewById(R.id.imageView4);
        imageClear = (ImageView) findViewById(R.id.imageView2);
        layout = (LinearLayout) findViewById(R.id.layout);
        layoutSearch = (LinearLayout) findViewById(R.id.layout1);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        layoutSearch.removeAllViews();
        readDB();

        View.OnClickListener onClickPlus = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                layoutSearch.removeAllViews();
                layout.setVisibility(View.VISIBLE);

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        };

        buttonPlus.setOnClickListener(onClickPlus);

        View.OnClickListener onClickEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                editText.setText("-- on 'Edit' clicked --");
            }
        };

        buttonEdit.setOnClickListener(onClickEdit);

        View.OnClickListener onClickDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v){
//                final int itemsLength = layout.getChildCount();
//
//                for(int i = 0; i < itemsLength; i++){
//                    CheckBox item = (CheckBox) layout.getChildAt(i);
//                    if(item.isChecked()){
//                        //database.delete(DBHelper.TABLE_NOTES, DBHelper.KEY_TITLE
//                                //+ "=" + item.getText(), null);
//                        layout.removeView(item);
//                        editText.setText("count");
//                    }
//                }
            }
        };

        buttonDelete.setOnClickListener(onClickDelete);


        CompoundButton.OnCheckedChangeListener onCheck = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int count = 0;
                layoutSearch.removeAllViews();
                layout.setVisibility(View.VISIBLE);

                final int itemsLength = layout.getChildCount();
                for(int i = 0; i < itemsLength; i++) {
                    CheckBox item = (CheckBox) layout.getChildAt(i);

                    if (item.isChecked() && count == 0) {
                        buttonDelete.setVisibility(View.VISIBLE);
                        buttonEdit.setVisibility(View.VISIBLE);
                        count++;
                    }else {
                        if (item.isChecked()) {
                            buttonEdit.setVisibility(View.INVISIBLE);
                            buttonDelete.setVisibility(View.VISIBLE);
                            count++;
                        }
                    }
                }

                if(count == 0){
                    buttonDelete.setVisibility(View.INVISIBLE);
                    buttonEdit.setVisibility(View.INVISIBLE);
                }
            }
        };

        for(int i = 0; i < layout.getChildCount(); i++) {
            CheckBox item = (CheckBox) layout.getChildAt(i);
            item.setOnCheckedChangeListener(onCheck);
        }


        View.OnClickListener onClickSearch = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Editable text = editText.getText();
                final int itemsLength = layout.getChildCount();

                layoutSearch.removeAllViews();
                layout.setVisibility(View.VISIBLE);

                for(int i = 0; i < itemsLength; i++){
                    CheckBox item = (CheckBox) layout.getChildAt(i);
                    item.setVisibility(View.VISIBLE);
                }

                if(text.length() != 0){
                    for (int i = 0; i < itemsLength; i++) {
                        CheckBox item = (CheckBox) layout.getChildAt(i);
                        CheckBox searchItem = new CheckBox(MainActivity.this);
                        searchItem.setText(item.getText());

                        if (item.getText().toString().contains(text.toString())) {
                            layoutSearch.addView(searchItem);
                        }
                    }
                    layout.setVisibility(View.INVISIBLE);
                }
            }
        };

        imageSearch.setOnClickListener(onClickSearch);

        View.OnClickListener onClickClear = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final int itemsLength = layout.getChildCount();
                layoutSearch.removeAllViews();
                layout.setVisibility(View.VISIBLE);

                for (int i = 0; i < itemsLength; i++) {
                    CheckBox item = (CheckBox) layout.getChildAt(i);
                    item.setVisibility(View.VISIBLE);
                }

                editText.setText("");
            }
        };

        imageClear.setOnClickListener(onClickClear);
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        layoutSearch.removeAllViews();
        layout.setVisibility(View.VISIBLE);
        readDB();
    }

    void readDB(){
        Cursor cursor = database.query(DBHelper.TABLE_NOTES, null,
                null, null, null, null, null);

        if(cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
            do {
                CheckBox newNote = new CheckBox(this);
                newNote.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newNote.setClickable(true);
                newNote.setFocusable(true);
                newNote.setText(cursor.getString(titleIndex));

                final int itemsLength = layout.getChildCount();
                int count = 0;

                for (int i = 0; i < itemsLength; i++) {
                    CheckBox item = (CheckBox) layout.getChildAt(i);
                    if(item.getText() != newNote.getText()){
                        count++;
                    }
                }

                if(count == itemsLength){
                    layout.addView(newNote);
                }
            } while (cursor.moveToNext());
        }
    }
}
