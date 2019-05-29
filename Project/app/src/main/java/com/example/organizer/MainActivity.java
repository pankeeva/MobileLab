package com.example.organizer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.text.Editable;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button buttonPlus, buttonDelete, buttonEdit, buttonSignIn;
    EditText editText;
    ImageView imageSearch, imageClear;
    LinearLayout layout;

    public static SQLiteDatabase database;
    public static DBHelper dbHelper;

    boolean signIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buttonPlus = (Button)findViewById(R.id.button);
        buttonEdit = (Button)findViewById(R.id.button2);
        buttonDelete = (Button)findViewById(R.id.button1);
        buttonSignIn = (Button) findViewById(R.id.button3);
        editText = (EditText)findViewById(R.id.textView2);
        imageSearch = (ImageView) findViewById(R.id.imageView4);
        imageClear = (ImageView) findViewById(R.id.imageView2);
        layout = (LinearLayout) findViewById(R.id.layout);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();


        readDB();

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                signIn = intent.getBooleanExtra("sign in", false);
            }
        });

        View.OnClickListener onClickPlus = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("text", "");
                startActivity(intent);
            }
        };

        buttonPlus.setOnClickListener(onClickPlus);
        buttonEdit.setOnClickListener(onClickEdit);
        buttonDelete.setOnClickListener(onClickDelete);


        View.OnClickListener onClickSearch = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Editable text = editText.getText();

                layout.removeAllViews();
                readDB();

                final int itemsLength = layout.getChildCount();

                CheckBox found[] = new CheckBox[itemsLength];
                int index = 0;

                if(text.length() != 0){
                    for (int i = 0; i < itemsLength; i++) {
                        CheckBox item = (CheckBox) layout.getChildAt(i);
                        CheckBox searchItem = new CheckBox(MainActivity.this);
                        searchItem.setText(item.getText());

                        if (!item.getText().toString().contains(text.toString())) {
                            found[index] = item;
                            index++;
                        }
                    }
                }

                for(int i = 0; i < index; i++){
                    layout.removeView(found[i]);
                }
            }
        };

        imageSearch.setOnClickListener(onClickSearch);

        View.OnClickListener onClickClear = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                editText.setText("");
                layout.removeAllViews();
                readDB();
            }
        };

        imageClear.setOnClickListener(onClickClear);
    }

    View.OnClickListener onClickEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            final int itemsLength = layout.getChildCount();
            Cursor cursor = database.query(DBHelper.TABLE_NOTES, null,
                    null, null, null, null, null);

            for(int i = 0; i < itemsLength; i++){
                CheckBox item = (CheckBox) layout.getChildAt(i);
                if(item.isChecked()){
                    cursor.moveToFirst();
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
                    int textIndex = cursor.getColumnIndex(DBHelper.KEY_TEXT);
                    do {
                        if(item.getText().equals(cursor.getString(titleIndex))) {
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            intent.putExtra("id", cursor.getInt(idIndex));
                            intent.putExtra("title", item.getText());
                            intent.putExtra("text", cursor.getString(textIndex));
                            startActivity(intent);
                            break;
                        }
                    } while (cursor.moveToNext());

                    break;
                }
            }
        }
    };

    View.OnClickListener onClickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            final int itemsLength = layout.getChildCount();

            CheckBox toDelete[] = new CheckBox[itemsLength];
            int index = 0;

            Cursor cursor = database.query(DBHelper.TABLE_NOTES, null,
                    null, null, null, null, null);

            for(int i = 0; i < itemsLength; i++){
                CheckBox item = (CheckBox) layout.getChildAt(i);
                if(item.isChecked()){
                    cursor.moveToFirst();
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
                    do {
                        if(item.getText().equals(cursor.getString(titleIndex))) {
                            database.delete(DBHelper.TABLE_NOTES, DBHelper.KEY_ID
                                    + "=" + cursor.getInt(idIndex), null);

                            toDelete[index] = item;
                            index++;
                            break;
                        }
                    } while (cursor.moveToNext());

                    break;
                }
            }

            for(int i = 0; i < index; i++){
                layout.removeView(toDelete[i]);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener onCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int count = 0;

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

    @Override
    protected void onRestart(){
        super.onRestart();
        buttonEdit.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.INVISIBLE);
        layout.removeAllViews();
        readDB();
    }

    void readDB(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    editText.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError error) { }
            });

            //return;
        }


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

                layout.addView(newNote);

                newNote.setOnCheckedChangeListener(onCheck);

            } while (cursor.moveToNext());
        }
    }
}
