package jp.ac.ecc.sk3a17.sns.Exercise;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    //ダミーデータ
    private List<Map<String, Object>> _menuList;

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

        //デバックデータ数確認
        TextView textCount = view.findViewById(R.id.textCount);
        textCount.setText(selected_day);

        //カレンダークリックイベントリスナーをセット
        CalendarView calenderview = view.findViewById(R.id.calendarView);
        calenderview.setOnDateChangeListener(new selectedDay());

        //adapterの作成　テストデータ表示
        _menuList = createMenuList();
        String[] from = {"name", "rep", "set"};
        int[] to = {R.id.tvDayMenuExercise, R.id.tvDayMenuRep, R.id.tvDayMenuSet};
        SimpleAdapter adapter = new SimpleAdapter(getContext(), _menuList, R.layout.row, from, to);
        ListView lvMenu = view.findViewById(R.id.lvExercise);
        lvMenu.setAdapter(adapter);

        return view;

    }

    //カレンダービュープライベートクラス
    private class selectedDay implements CalendarView.OnDateChangeListener {
        //カレンダービューの日付クリックリスナー
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            selected_day = String.valueOf(year) + "/" + (month + 1) + "/" + dayOfMonth;
            TextView textCount = view.findViewById(R.id.textCount);
            Log.d("date", selected_day);
            Toast.makeText(getContext(), selected_day, Toast.LENGTH_SHORT).show();
        }
    }


    //リストを作成するメソッド
    private List<Map<String, Object>> createMenuList() {
        //Listオブジェクト用意
        List<Map<String, Object>> menuList = new ArrayList<>();
        //Mapオブジェクト用意、menuListへのデータ登録
        Map<String, Object> menu = new HashMap<>();
        menu.put("name", "腕立て");
        menu.put("rep", 8);
        menu.put("set", "3");
        menuList.add(menu);

        menu = new HashMap<>();
        menu.put("name", "腹筋");
        menu.put("rep", 8);
        menu.put("set", "2");
        menuList.add(menu);

        menu = new HashMap<>();
        menu.put("name", "スクワット");
        menu.put("rep", 6);
        menu.put("set", "3");
        menuList.add(menu);

        menu = new HashMap<>();
        menu.put("name", "懸垂");
        menu.put("rep", 6);
        menu.put("set", "3");
        menuList.add(menu);

        menu = new HashMap<>();
        menu.put("name", "背筋");
        menu.put("rep", 6);
        menu.put("set", "3");
        menuList.add(menu);

        return menuList;
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

    //データベースをにデータ格納
    public int recordInsert() {
        //データベースヘルパーオブジェクト生成
        DatabaseHelper helper = new DatabaseHelper(getContext());
        //データベース接続オブジェクト生成
        SQLiteDatabase db = helper.getWritableDatabase();

        int count = 0;
        try {
            //すでに登録されているデータカウント
            Cursor cursor = db.rawQuery(String.format("SELECT COUNT(*) FROM exerciseList"), null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } finally {
            //データベース接続オブジェクト開放
            db.close();
        }


        return count;

    }

}
