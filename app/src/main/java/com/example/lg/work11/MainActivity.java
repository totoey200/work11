package com.example.lg.work11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MyCanvas canvas;
    CheckBox stamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("그림판");

        canvas = (MyCanvas)findViewById(R.id.cavas);
        stamp = (CheckBox)findViewById(R.id.stamp);
        stamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                canvas.setStamp(isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,0,"Bluring");
        menu.add(1,2,0,"Coloring");
        menu.add(1,3,0,"Pen Width Big");
        menu.add(0,4,0,"Pen Color RED");
        menu.add(0,5,0,"Pen Color BLUE");
        menu.setGroupCheckable(1,true,false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            if(item.isChecked()) {
                item.setChecked(false);
                canvas.setBluring(false);
            }
            else{
                item.setChecked(true);
                canvas.setBluring(true);
            }
        }
        else if(item.getItemId() == 2){
            if(item.isChecked()) {
                item.setChecked(false);
                canvas.setColoring(false);
            }
            else{
                item.setChecked(true);
                canvas.setColoring(true);
            }
        }
        else if(item.getItemId() == 3){
            if(item.isChecked()) {
                item.setChecked(false);
                canvas.setBig(false);
            }
            else{
                item.setChecked(true);
                canvas.setBig(true);
            }
        }
        else if(item.getItemId() == 4){
            canvas.setLineColor("RED");
        }
        else if(item.getItemId() == 5){
            canvas.setLineColor("BLUE");
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClick(View v){
        if(v.getId() == R.id.eraser){
            canvas.clear();
        }
        else if(v.getId() == R.id.save){
            save("saved.jpg");
        }
        else if(v.getId() == R.id.open){ // 비트맵파일이 있으면 불러오기
            File file = new File(getFilesDir()+"saved.jpg");
            if(file.isFile()){
                canvas.clear();
                Bitmap img = BitmapFactory.decodeFile(getFilesDir()+"saved.jpg");
                canvas.openimg(img);
                Toast.makeText(this, "불러오기 성공", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            canvas.setOpType(v.getTag().toString());
            stamp.setChecked(true);
        }
    }
    public boolean save(String file_name) {//비트맵파일 저장
        Bitmap nowBitmap = canvas.getBitmap();
        try {
            FileOutputStream out = new FileOutputStream(getFilesDir() + file_name);
            nowBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
        return false;
    }

}
