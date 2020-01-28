package jp.ac.ecc.sk3a17.sns.Exercise;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

public class ExerciseAddActivity extends AppCompatActivity {

    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_add);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");


        //ソートボタンへクリックイベントのリスナーをセット
        Button chestButton = findViewById(R.id.chestButton);
        chestButton.setOnClickListener(new sortButtonClick());
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new sortButtonClick());
        Button shoulderButton = findViewById(R.id.shoulderButton);
        shoulderButton.setOnClickListener(new sortButtonClick());
        Button armsButton = findViewById(R.id.armsButton);
        armsButton.setOnClickListener(new sortButtonClick());
        Button absButton = findViewById(R.id.absButton);
        absButton.setOnClickListener(new sortButtonClick());
        Button legsButton = findViewById(R.id.legsButton);
        legsButton.setOnClickListener(new sortButtonClick());
        Button suportsButton = findViewById(R.id.suportsButton);
        suportsButton.setOnClickListener(new sortButtonClick());
        Button waterButton = findViewById(R.id.waterButton);
        waterButton.setOnClickListener(new sortButtonClick());
        Button winterButton = findViewById(R.id.winterButton);
        winterButton.setOnClickListener(new sortButtonClick());
        Button danceButton = findViewById(R.id.danceButton);
        danceButton.setOnClickListener(new sortButtonClick());
        Button etcButton = findViewById(R.id.etcButton);
        etcButton.setOnClickListener(new sortButtonClick());
        Button walkButton = findViewById(R.id.walkButton);
        walkButton.setOnClickListener(new sortButtonClick());
        Button runButton = findViewById(R.id.runButton);
        runButton.setOnClickListener(new sortButtonClick());
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new sortButtonClick());

        //ListViewへクリックイベントセット
        ListView lv = findViewById(R.id.lvExerciseList);
        lv.setOnItemClickListener(new ListItemClick());

    }

    //データベース接続・ソートした内容をselect・引数はint part
    public List<Map<String, Object>> selectData(int part) {

        int id = 0;
        String name = "";
        float mets = 0;
        float second = 0;

        //Listオブジェクト用意
        List<Map<String, Object>> menuList = new ArrayList<>();

        //データベース接続取得
        DatabaseHelper helper = new DatabaseHelper(ExerciseAddActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        //データベース検索用のSQL文作成
        String sql = "SELECT * FROM exerciseList WHERE part = " + part;
        //カーソル型でdataを取得
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            //curor型から値取り出し
            id = cursor.getInt(cursor.getColumnIndex("_id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            mets = cursor.getFloat(cursor.getColumnIndex("mets"));
            second = cursor.getFloat(cursor.getColumnIndex("second"));
            //Mapオブジェクト用意、menuListへのデータ登録
            Map<String, Object> menu = new HashMap<>();
            menu.put("id", id);
            menu.put("name", name);
            menu.put("mets", mets);
            menu.put("second", second * 10);
            //MapをListへ追加
            menuList.add(menu);
        }
        return menuList;
    }

    //seletDataで出したデータをListViewに接続
    public void setListView(List<Map<String, Object>> menuList) {
        //ListViewへの表示
        //fromはListViewに入れる対象データ
        String from[] = {"name", "mets", "second"};
        //toはデータを入れるListView内の部品
        int to[] = {R.id.tvExerciseName, R.id.tvExerciseMets, R.id.tvExerciseSecond};
        //SimpleAdapterでListViewに接続
        SimpleAdapter adapter = new SimpleAdapter(ExerciseAddActivity.this, menuList, R.layout.rowadd, from, to);
        ListView lv = findViewById(R.id.lvExerciseList);
        lv.setAdapter(adapter);
    }

    //ソートボタンクリックイベントリスナークラス
    private class sortButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //データベースへの接続取得
            DatabaseHelper helper = new DatabaseHelper(ExerciseAddActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();

            //IDを元に押されたボタンを判別
            switch (view.getId()) {
                case R.id.chestButton:
                    setListView(selectData(1));
                    break;
                case R.id.backButton:
                    setListView(selectData(2));
                    break;
                case R.id.shoulderButton:
                    setListView(selectData(3));
                    break;
                case R.id.armsButton:
                    setListView(selectData(4));
                    break;
                case R.id.absButton:
                    setListView(selectData(5));
                    break;
                case R.id.legsButton:
                    setListView(selectData(6));
                    break;
                case R.id.etcButton:
                    setListView(selectData(7));
                    break;
                case R.id.suportsButton:
                    setListView(selectData(8));
                    break;
                case R.id.waterButton:
                    setListView(selectData(9));
                    break;
                case R.id.winterButton:
                    setListView(selectData(10));
                    break;
                case R.id.danceButton:
                    setListView(selectData(11));
                    break;
                case R.id.walkButton:
                    setListView(selectData(12));
                    break;
                case R.id.runButton:
                    setListView(selectData(13));
                    break;

                default:
                    finish();
                    break;
            }
        }
    }

    //ListViewクリックリスナークラス
    private class ListItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //タップされた行データ取得　SimpleAdapterはMap型でくる
            Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
            //注文確認
            order(item);
        }
    }

    //order確認メソッド
    private void order(Map<String, Object> menu) {
        //クリックした名前、強度、秒取得
        String name = (String) menu.get("name");
        float mets = (float) menu.get("mets");
        float second = (float) menu.get("second");

        //Intentオブジェクト生成
        Intent intent = new Intent(ExerciseAddActivity.this, ExerciseOrderActivity.class);
        //送るデータを格納
        intent.putExtra("date", date);
        intent.putExtra("name", name);
        intent.putExtra("mets", mets);
        intent.putExtra("second", second);
        //order確認の画面起動
        startActivity(intent);
    }

}
