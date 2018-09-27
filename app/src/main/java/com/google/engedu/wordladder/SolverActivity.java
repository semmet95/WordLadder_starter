package com.google.engedu.wordladder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.engedu.worldladder.R;

public class SolverActivity extends AppCompatActivity {
    String[] words;
    TextView startTextView, endTextView;
    LinearLayout childLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_solver);
        Intent intent=getIntent();
        words=intent.getStringArrayExtra("words");
        startTextView=(TextView)findViewById(R.id.startTextView);
        endTextView=(TextView)findViewById(R.id.endTextView);
        startTextView.setText(intent.getStringExtra("start"));
        endTextView.setText(intent.getStringExtra("end"));
        childLayout = new LinearLayout(this);
        childLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        childLayout.setOrientation(LinearLayout.VERTICAL);
        for(int i=0;i<words.length;i++) {
            EditText temp=new EditText(this);
            temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            temp.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Large);
            temp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(PathDictionary.words.contains(s.toString())){}
                }
            });
            childLayout.addView(temp);
        }
        LinearLayout parentLayout=(LinearLayout)findViewById(R.id.parentLayout);
        parentLayout.addView(childLayout, 1);
    }

    public void onSolve(View v) {
        for(int i=0;i<words.length;i++) {
            ((EditText)childLayout.getChildAt(i)).setText(words[i]);
        }
    }
}
