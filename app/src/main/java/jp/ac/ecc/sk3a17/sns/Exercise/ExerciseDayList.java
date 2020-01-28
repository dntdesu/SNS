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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

public class ExerciseDayList extends AppCompatActivity {

    //ExerciseFragmentから送られてきた選択された日付データ
    private String selected_day = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_day_list);

        //日付データ取得
        Intent intent = getIntent();
        selected_day = intent.getStringExtra("selectedDay");
        TextView text = findViewById(R.id.textView);
        text.setText(selected_day);

        DatabaseHelper helper = new DatabaseHelper(ExerciseDayList.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int id = 0;
        String name ="";
        float mets = 0;
        float second;
        int part = 0;
        int rep = 0;
        int set_time = 0;
        //Listオブジェクト用意
        List<Map<String, Object>> menuList = new ArrayList<>();

        //選択された日の運動の種目検索
        String sql = "SELECT * FROM day_exercise WHERE date = ?";
        //配列化
        String[] args = {selected_day};
        //検索実行
        Cursor c = db.rawQuery(sql, args);
        Toast.makeText(ExerciseDayList.this, "yhaa", Toast.LENGTH_SHORT);

        //次のデータが存在する間ループ　日付、種目名、回数、セット数取得
        while (c.moveToNext()){

            Toast.makeText(ExerciseDayList.this, "yha", Toast.LENGTH_SHORT);
            selected_day = c.getString(c.getColumnIndex("date"));
            name = c.getString(c.getColumnIndex("name"));
            rep = c.getInt(c.getColumnIndex("rep"));
            set_time = c.getInt(c.getColumnIndex("set_time"));

            Log.d("data", selected_day + name + rep + set_time);
            //Mapオブジェクト用意、menuListへのデータ登録
            Map<String, Object> menu = new HashMap<>();
            menu.put("date", selected_day);
            menu.put("name", name);
            menu.put("rep", rep);
            menu.put("set_time", set_time);
            menuList.add(menu);
        }

        //戻るボタンクリックイベントセット
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new backButtonClick());
        //追加ボタンクリックイベントセット
        Button addButton = findViewById(R.id.addExerciseButton);
        addButton.setOnClickListener(new addButtonClick());

    }

    @Override
    public void onRestart() {
        super.onRestart();
        //Listオブジェクト用意
        List<Map<String, Object>> menuList = new ArrayList<>();

        //データベース接続
        DatabaseHelper helper = new DatabaseHelper(ExerciseDayList.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        //選択された日の運動の種目検索
        String sql = "SELECT * FROM day_exercise WHERE date = ?";
        //配列化
        String[] args = {selected_day};
        //検索実行
        Cursor c = db.rawQuery(sql, args);

        //次のデータが存在する間ループ　日付、種目名、回数、セット数取得
        while (c.moveToNext()) {
            selected_day = c.getString(c.getColumnIndex("date"));
            String name = c.getString(c.getColumnIndex("name"));
            int rep = c.getInt(c.getColumnIndex("rep"));
            int set_time = c.getInt(c.getColumnIndex("set_time"));

            Log.d("data", selected_day + name + rep + set_time);
            //Mapオブジェクト用意、menuListへのデータ登録
            Map<String, Object> menu = new HashMap<>();
            menu.put("date", selected_day);
            menu.put("name", name);
            menu.put("rep", rep);
            menu.put("set_time", set_time);
            menuList.add(menu);
        }

        //ListViewへの表示
        String from[] = {"name", "rep", "set_time"};
        int to[] = {R.id.tvDayMenuExercise, R.id.tvDayMenuRep, R.id.tvDayMenuSet};
        SimpleAdapter adapter = new SimpleAdapter(ExerciseDayList.this, menuList, R.layout.row, from, to);
        ListView lv = findViewById(R.id.lvDayExercise);
        lv.setAdapter(adapter);
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
            //メニュー追加画面へ移動
            Intent intent = new Intent(ExerciseDayList.this, ExerciseAddActivity.class);
            intent.putExtra("date", selected_day);
            startActivity(intent);
        }
    }
}

//検索Select処理をメソッドかする