package com.example.organizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    Button buttonPlus;
    EditText editText;
    Button buttonDelete;
    Button buttonEdit;

    CheckBox ids[] = new CheckBox[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlus = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.textView2);

        View.OnClickListener onClickPlus = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                editText.setText("-- on '+' clicked --");
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
                editText.setText("-- on 'Delete' clicked --");
            }
        };

        buttonDelete.setOnClickListener(onClickDelete);



        ids[0] = (CheckBox)findViewById(R.id.checkBox5);
        ids[1] = (CheckBox)findViewById(R.id.checkBox6);
        ids[2] = (CheckBox)findViewById(R.id.checkBox7);
        ids[3] = (CheckBox)findViewById(R.id.checkBox8);
        ids[4] = (CheckBox)findViewById(R.id.checkBox9);

        buttonDelete = (Button)findViewById(R.id.button1);
        buttonEdit = (Button)findViewById(R.id.button2);


        CompoundButton.OnCheckedChangeListener onCheck = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int count = 0;

                for(int i = 0; i < ids.length; i++){
                    if(ids[i].isChecked()){
                        buttonDelete.setVisibility(View.VISIBLE);
                        buttonEdit.setVisibility(View.VISIBLE);
                        count++;
                    }
                }

                if(count == 0){
                    buttonDelete.setVisibility(View.INVISIBLE);
                    buttonEdit.setVisibility(View.INVISIBLE);
                }
            }
        };

        for(int i = 0; i < ids.length; i++){
            ids[i].setOnCheckedChangeListener(onCheck);
        }
    }
}
