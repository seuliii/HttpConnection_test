package org.techtown.yogatcameralayoutmidpro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast toast = Toast.makeText(getApplicationContext(),"안녕하세요, 반갑습니다", Toast.LENGTH_LONG);
        toast.setMargin(50,50);
        toast.show();
    }
}