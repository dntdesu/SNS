package jp.ac.ecc.sk3a17.sns.Exercise;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment {
    private View view;

    //選択されたエクササイズの主キーIDを表すフィールド
    int _exerciseId = -1;
    //エクササイズの名前
    String _exerciseName = "";
    //保存ボタンフィールド
    Button _btnSave;

    //本日の日付取得
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    String selected_day = sdf.format(cal.getTime());

    public ExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_exercise, container, false);

        //編集ボタン登録
        Button editbButton = view.findViewById(R.id.editButton);
        //リスナー登録
        editbButton.setOnClickListener(new editButtonClick());

        //カレンダークリックイベントリスナーをセット
        CalendarView calenderview = view.findViewById(R.id.calendarView);
        calenderview.setOnDateChangeListener(new selectedDay());

        //データ検索→ListViewへ登録
        setDataToListView(selectExercise());

        return view;

    }

    //カレンダービュープライベートクラス
    private class selectedDay implements CalendarView.OnDateChangeListener {
        //カレンダービューの日付クリックリスナー
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            selected_day = String.valueOf(year) + "/" + (month + 1) + "/" + dayOfMonth;
            //データ検索→ListViewへ登録
            setDataToListView(selectExercise());
        }
    }

    //編集ボタンクリックリスナー
    //Xmlにイベントを登録するやり方ではだめだった
    private class editButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //指定日メニューの編集画面
            Intent intent = new Intent(getActivity(), ExerciseDayList.class);
            //指定日String型で値渡し
            intent.putExtra("selectedDay", selected_day);
            startActivity(intent);
        }
    }

    //データベースから指定日の運動検索
    public List<Map<String, Object>> selectExercise() {
        //データを取り出すようの変数
        String name = "";
        int rep = 0;
        int set_time = 0;
        //戻り値用のList
        List<Map<String, Object>> menuList = new ArrayList<>();

        //データベースへの接続取得
        DatabaseHelper helper = new DatabaseHelper(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            //検索文字列用意
            String sql = "SELECT * FROM day_exercise WHERE date = ?";
            //配列化
            String args[] = {selected_day};
            //検索実行
            Cursor cursor = db.rawQuery(sql, args);

            //次のデータが存在する間ループ　日付、種目名、回数、セット数取得
            while (cursor.moveToNext()) {
                //Cursor型からデータ取り出し
                selected_day = cursor.getString(cursor.getColumnIndex("date"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                rep = cursor.getInt(cursor.getColumnIndex("rep"));
                set_time = cursor.getInt(cursor.getColumnIndex("set_time"));

                //Mapオブジェクト用意、menuListへのデータ登録
                Map<String, Object> menu = new HashMap<>();
                menu.put("date", selected_day);
                menu.put("name", name);
                menu.put("rep", rep);
                menu.put("set_time", set_time);
                menuList.add(menu);
            }

        } finally {
            db.close();
        }

        return menuList;
    }

    //データをListViewへadapter登録
    public void setDataToListView(List<Map<String, Object>> menuList) {
        //adapterの作成　テストデータ表示
        String[] from = {"name", "rep", "set"};
        int[] to = {R.id.tvDayMenuExercise, R.id.tvDayMenuRep, R.id.tvDayMenuSet};
        SimpleAdapter adapter = new SimpleAdapter(getContext(), menuList, R.layout.row, from, to);
        ListView lvMenu = view.findViewById(R.id.lvExercise);
        lvMenu.setAdapter(adapter);
    }

}
