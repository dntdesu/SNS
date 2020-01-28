package jp.ac.ecc.sk3a17.sns.Exercise;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

public class ExerciseOrderActivity extends AppCompatActivity {

    //データ登録用変数
    private String date;
    private String name;
    private float second;
    private int rep;
    private int set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_order);

        //運動追加画面からデータうけとり
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        second = intent.getFloatExtra("second", 0);

        EditText timeEdit = (EditText) findViewById(R.id.timeEdit);
        //timeEditは数字のみ入力可能
        timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        //イベント決定ボタン設定
        Button decideButton = findViewById(R.id.decideButton);
        decideButton.setOnClickListener(new decideButtonClick());
    }

    //イベントリスナークラス
    private class decideButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Spinner repSpinner = findViewById(R.id.repSpinner);
            Spinner setSpinner = findViewById(R.id.setSpinner);
            EditText timeEdit = findViewById(R.id.timeEdit);


            //すぴなーから値取得 intに変換
            rep = Integer.parseInt((String) repSpinner.getSelectedItem());
            set = Integer.parseInt((String) setSpinner.getSelectedItem());

            //選ばれた日付のトレーニングとして登録
            DatabaseHelper helper = new DatabaseHelper(ExerciseOrderActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            SQLiteStatement stmt;

            try {
                //日毎運動テーブルに同じデータがすでにあるか検索
                String sql = "SELECT * FROM day_exercise WHERE date = ? AND name = ?";
                //？用の配列用意
                String args[] = {date, name};
                //検索実行
                Cursor c = db.rawQuery(sql, args);

                //データがすでにあるか判定 あったらtrue
                if (c.moveToNext()) {
                    //削除用SQL文用意
                    String sqlDelete = "DELETE FROM day_exercise WHERE date = ? AND name = ?";
                    //SQL文を元にプリペアステートメントを取得
                    stmt = db.compileStatement(sqlDelete);
                    //変数バインド
                    stmt.bindString(1, date);
                    stmt.bindString(2, name);
                    //削除SQL実行
                    stmt.executeUpdateDelete();

                    Toast.makeText(ExerciseOrderActivity.this, "削除完了", Toast.LENGTH_SHORT);
                }

                //インサート文
                String sqlInsert = "INSERT INTO day_exercise(date,name,rep,set_time) VALUES(?,?,?,?)";
                //プリペアステートメント所得
                stmt = db.compileStatement(sqlInsert);
                //変数バインド
                stmt.bindString(1, date);
                stmt.bindString(2, name);
                stmt.bindLong(3, rep);
                //筋トレかそれ以外か
                if (second == 0) {
                    second = Integer.parseInt(timeEdit.getText().toString());
                    stmt.bindLong(4, set);
                } else {
                    stmt.bindLong(4, set);
                }
                //SQL実行
                stmt.executeInsert();

                Toast.makeText(ExerciseOrderActivity.this, "追加完了", Toast.LENGTH_SHORT).show();

            } finally {
                //データベース接続オブジェクト開放
                db.close();
            }

            //追加終了,追加運動選択画面へ
            Intent intent = new Intent(ExerciseOrderActivity.this, ExerciseAddActivity.class);
            //add確認の画面起動
            startActivity(intent);
        }
    }
}
