package com.example.organizer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity {

    ImageView imageBack;
    Button buttonSave;
    TextView title, textArea, editTitle, editTextArea;

    int editId;

    public DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        imageBack = (ImageView) findViewById(R.id.imageView);
        buttonSave = (Button) findViewById(R.id.button);
        title = (TextView) findViewById(R.id.textView2);
        textArea = (TextView) findViewById(R.id.textView3);

        editTitle = new TextView(this);
        editTextArea = new TextView(this);

        editId = getIntent().getIntExtra("id", 0);
        title.setText(getIntent().getStringExtra("title"));
        textArea.setText(getIntent().getStringExtra("text"));

        editTitle.setText(title.getText());
        editTextArea.setText(textArea.getText());

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
                    if(FirebaseAuth.getInstance().getCurrentUser() != null)
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        String title2 = editTitle.getText().toString();
                        String text2 = editTextArea.getText().toString();

                        UserInformation userInformation = new UserInformation(title2, text2);
                        databaseReference.child(user.getUid()).setValue(userInformation);

                       // finish();
                        //return;
                    }

                    if(editTextArea.length() != 0 && editTitle.length() != 0){
                        Cursor cursor = MainActivity.database.query(DBHelper.TABLE_NOTES,   null,
                                null, null, null, null, null);

                        ContentValues cv = new ContentValues();
                        cv.put("title", title.getText().toString());
                        cv.put("text", textArea.getText().toString());

                        cursor.moveToFirst();
                        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                        do {
                            if(cursor.getInt(idIndex) == editId){
                                //String stringId = Integer.toString(editId);
                                MainActivity.database.update(DBHelper.TABLE_NOTES, cv,
                                        DBHelper.KEY_ID + "=" + editId, null);
                                break;
                            }
                        } while (cursor.moveToNext());
                    }else{
                        String titleStr = title.getText().toString();
                        String textStr = textArea.getText().toString();

                        SQLiteDatabase database = MainActivity.dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(DBHelper.KEY_TITLE, titleStr);
                        contentValues.put(DBHelper.KEY_TEXT, textStr);

                        database.insert(DBHelper.TABLE_NOTES, null, contentValues);
                    }

                    finish();
                }
            }
        };

        buttonSave.setOnClickListener(onClickSave);

        //MainActivity.dbHelper.close();
    }
}
