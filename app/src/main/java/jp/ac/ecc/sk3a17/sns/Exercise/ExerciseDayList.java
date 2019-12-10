package jp.ac.ecc.sk3a17.sns.Exercise;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

public class ExerciseDayList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_day_list);

        //日付データ取得
        Intent intent = getIntent();
        String date = intent.getStringExtra("selectedDay");
        TextView text = findViewById(R.id.textView);
        text.setText(date);

        DatabaseHelper helper = new DatabaseHelper(ExerciseDayList.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int id = 0;
        String name ="";
        float mets = 0;
        float second;
        int part = 0;

        //Listオブジェクト用意
        List<Map<String, Object>> menuList = new ArrayList<>();


        String sql = "SELECT * FROM exerciseList WHERE part = 1";
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()){
            id = c.getInt(c.getColumnIndex("_id"));
            name = c.getString(c.getColumnIndex("name"));
            mets = c.getFloat(c.getColumnIndex("mets"));
            second = c.getFloat(c.getColumnIndex("second"));
            part = c.getInt(c.getColumnIndex("part"));

            Log.d("data",id + name + mets + second + part);
            //Mapオブジェクト用意、menuListへのデータ登録
            Map<String, Object> menu = new HashMap<>();
            menu.put("id", id);
            menu.put("name", name);
            menu.put("mets", mets);
            menu.put("second",second);
            menu.put("part",part);
            menuList.add(menu);
        }


        //ListViewへの表示
        String from[] = {"name","_id","mets"};
        int to[] = {R.id.tvDayMenuExercise,R.id.tvDayMenuRep,R.id.tvDayMenuSet};
        SimpleAdapter adapter = new SimpleAdapter(ExerciseDayList.this, menuList ,R.layout.row, from, to);
        ListView lv = findViewById(R.id.lvDayExercise);
        lv.setAdapter(adapter);

        //戻るボタンクリックイベントセット
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new backButtonClick());
        //追加ボタンクリックイベントセット
        Button addButton = findViewById(R.id.addExerciseButton);
        addButton.setOnClickListener(new addButtonClick());

    }

    //戻るボタンクリックイベントクラス
    private class backButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    //運動追加ボタンクリックイベントクラス
    private class addButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }
}
