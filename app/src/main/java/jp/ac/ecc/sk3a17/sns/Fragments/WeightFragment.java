package jp.ac.ecc.sk3a17.sns.Fragments;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightFragment extends Fragment {

    private EditText editText;
    private TextView textView2;
    private TextView textView3;
    private ImageView imageView;
    private Button EntryButton;
    long weight;

    //データの初期化
    //mokuki mokutai gentai はWfragment2の値が入る
    private String mokuki = "";
    private String mokutai = "";
    private String gentai = "";
    //WeightFragmentで以前の値を入れておく
    //private String kiikan = "";
    //private String mookuta = "";
    //private String geentai = "";

    private String s = "";
    private String tt = "";

    private int cnt = 0;
    private String gtai;
    //入力されたやつを数値に変えた体重
    private double tai;
    private double bmi;
    //下2行がいるかいらないか分からない
    double shin = 150.2;
    double mtai = 58.0;
    Calendar cal = Calendar.getInstance();
    int month = cal.get(Calendar.MONTH) + 1;
    double shinm = shin / 100;
    double shinm2 = shinm * shinm;

    long data[] = new long[7];
    String date[] = new String[7];
    Date[] date2 = new Date[7];
    int goal[] = new int[7];
    long yosoku[] = new long[7];
    int meal;//食べた量
    int training;//動いた量
    int xKcal;//
    double ingestion;
    long tomorrow_weight;

    //インスタンス
    Wfragment2 fragment02 = new Wfragment2();


    public WeightFragment() {
        // Required empty public constructor
    }

    public static WeightFragment newInstance() {
        return new WeightFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weight, container, false);


        Bundle extras;
        extras = getArguments();

        //データバンドルが存在すれば
        if (extras != null) {
            mokuki = extras.getString("mokuki");
            mokutai = extras.getString("mokutai");
        }

        data[0] = 100;
        data[1] = 95;
        data[2] = 98;
        data[3] = 97;
        data[4] = 80;

        if (!mokutai.isEmpty()) {
            for (int i = 0; i < goal.length; i++) {
                goal[i] = Integer.parseInt(mokutai);
            }

        }

        //目標体重表示TextViewへ表示
        TextView tvGoalWeight = view.findViewById(R.id.tVGoalWeight);
        tvGoalWeight.setText("目標体重" + mokutai + "kg");

        graphDisplay(view);

        return view;
    }

    /*ボタン処理*/
    @Override
    public void onStart() {
        super.onStart();
        findViews();

        EntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //現在体重を表示

                try {
                    //目標体重まで
                    //現在の体重を入力、表示
                    String str = editText.getText().toString();
                    weight = Long.parseLong(str);

                    //目標体重までの残り表示
                    //textView3.setText("成功");
                    textView3.setText("あと" + (weight - 40) + " Kg");

                    //現在の体重を入力、表示
                    textView2.setText("現在 " + editText.getText() + " Kg");
                    tomorrow();

                    data[5] = weight;
                    yosoku[5] = weight;

                    //これをしたらグラフ表示できる
                    View v = (View) ((View) ((View) view.getParent()).getParent()).getParent();

                    //目標体重を変える
                    target_change();

                    //グラフを再表示
                    graphDisplay(v);

                    //画像切り替え
                    SwitchImages();

                    //editText内の文字を消す
                    editText.getEditableText().clear();

                } catch (Exception e) {
                    String str = "体重を入力してください";
                    Toast toast = (Toast.makeText(getContext(), str, Toast.LENGTH_LONG));
                    toast.show();
                }


                //入力ボタン押したらキーボードを消す
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            }
        });
    }

    /**/
    protected void findViews() {
        editText = (EditText) getActivity().findViewById(R.id.EntryWeight);//体重入力するtextBox
        EntryButton = (Button) getActivity().findViewById(R.id.EntryButton);//体重を入力するbutton
        textView2 = (TextView) getActivity().findViewById(R.id.textView2);//体重を表示するlabel
        textView3 = (TextView) getActivity().findViewById(R.id.textView3);//体重を表示するlabel
        imageView = (ImageView) getActivity().findViewById(R.id.imageView);//画像
    }

    /*
    グラフ表示メソッド
     */
    private void graphDisplay(View view) {

        LayoutInflater inf = LayoutInflater.from(getContext());
        LineChart lineChart = (LineChart) view.findViewById(R.id.line_chart);

        // X軸の値
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        int n = 0;

        for (int i = -5; i <= 1; i++) {
            calendar.add(Calendar.DATE, i);
            date2[n] = calendar.getTime();
            calendar.add(Calendar.DATE, -i);
            date[n] = sdf.format(date2[n++]) + "日";
        }

        ArrayList<String> xValues = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            xValues.add((date[i]));
        }


        // Y軸の設定
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        //データの値でY軸が変更される
        leftAxis.setStartAtZero(false);
        //Y軸の最大最小を決める
        leftAxis.setAxisMaxValue(120);
        leftAxis.setAxisMinValue(20);
        //Y軸の間隔
        leftAxis.setLabelCount(11, true);
        //右にはY軸いらないから消す
        rightAxis.setEnabled(false);


        //グラフに値を追加
        ArrayList<LineDataSet> dataSets = new ArrayList<>();

        //体重
        ArrayList<Entry> value = new ArrayList<>();
        LineDataSet valueDataSet = new LineDataSet(value, "体重");

        for (int j = 0; j < 6; j++) {
            value.add(new Entry(data[j], j));
        }
        valueDataSet.setColor(Color.rgb(0, 220, 180));
        valueDataSet.setValueTextSize(10f);


        //目標体重の線
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        LineDataSet set2 = new LineDataSet(yVals2, "目標");

        for (int i = 0; i < goal.length; i++) {
            yVals2.add(new Entry(goal[i], i));
        }
        set2.setColor(Color.rgb(164, 0, 0));
        set2.setValueTextSize(0f);


        //予測体重の線
        ArrayList<Entry> yosoku2 = new ArrayList<Entry>();
        LineDataSet set3 = new LineDataSet(yosoku2, "予測");

        for (int m = 0; m <= 4; m++) {
            yosoku[m] = data[m];
        }
        yosoku[6] = tomorrow_weight;

        for (int k = 0; k < yosoku.length; k++) {
            yosoku2.add(new Entry(yosoku[k], k));
        }
        set3.setColor(Color.rgb(250, 180, 0));
        set3.setValueTextSize(0f);

        valueDataSet.setDrawFilled(false);//グラフの塗りつぶしをするかしないか


        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(valueDataSet);

        lineChart.setData(new LineData(xValues, dataSets));


    }

    /*
    画像切り替えメソッド
     */
    private void SwitchImages() {
        double shin = 150.2;
        double shinm = shin / 100;
        double shinm2 = shinm * shinm;
        double bmi = weight / shinm2;
        if (bmi >= 40) {
            imageView.setImageResource(R.drawable.no4);
            Toast.makeText(getActivity(), "一番", Toast.LENGTH_SHORT).show();
        } else if (bmi >= 35 && bmi < 40) {
            imageView.setImageResource(R.drawable.no3);
            Toast.makeText(getActivity(), "二番", Toast.LENGTH_SHORT).show();
        } else if (bmi >= 30 && bmi < 35) {
            imageView.setImageResource(R.drawable.no2);
            Toast.makeText(getActivity(), "三番", Toast.LENGTH_SHORT).show();
        } /*else if (bmi >= 25 && bmi < 30) {
            imageView.setImageResource(R.drawable.no1);
            Toast.makeText(getActivity(), "四番", Toast.LENGTH_SHORT).show();
        } else if (bmi >= 18 && bmi < 25) {
            im1.setImageResource(R.drawable.yoga);
            Toast.makeText(getActivity(), "五番", Toast.LENGTH_SHORT).show();
        } */ else {
            imageView.setImageResource(R.drawable.no1);
        }
    }

    /*
    目標体重変更メソッド
     */
    private void target_change() {

        for (int i = 0; i < goal.length; i++) {
            goal[i] = goal[i];
        }
    }

    /*
    次の日の体重予測
    */
    private void tomorrow() {

        /////摂取と消費
        meal = 1;
        training = -69900;//-69900kcal消費していたら明日10kg減る
        ////

        xKcal = meal + (training);
        ingestion = xKcal / 6990;

        tomorrow_weight = weight + ((int) ingestion);
    }

    /*
    体重記録するときのDBInsert
    */
    private void databaseInsert() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        String toDay = sdf.format(calendar.getTime());//今日の日付をString型で格納

        //データベースヘルパーオブジェクト生成
        DatabaseHelper helper = new DatabaseHelper(getContext());
        //データベース接続オブジェクト生成
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            //まずは選択された運動のメモデータ削除。その後インサート
            //削除用SQL文用意
            String sqlDelete = "DELETE FROM weightdb WHERE date = ?";
            //SQL文を元にプリペアステートメントを取得
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            //変数バインド
            stmt.bindString(1, toDay);
            //SQL実行
            stmt.executeUpdateDelete();

            //インサート文
            String sqlInsert = "INSERT INTO weightDb(date, weight) VALUES(?,?)";//weight以外は他のページからとりたい
            //プリペアステートメント所得
            stmt = db.compileStatement(sqlInsert);
            //変数バインド
            stmt.bindString(1, toDay);//(今)
            stmt.bindLong(2, weight);//60
            //SQL実行
            stmt.executeInsert();

        } finally {
            //データベース接続オブジェクト開放
            db.close();
        }

    }

    /*
    DBのデータを取ってきてグラフに渡す
     */
     /*private  void databeseSelect(){
        //データベースヘルパーオブジェクト生成
        DatabaseHelper helper = new DatabaseHelper(getContext());
        //データベース接続オブジェクト生成
        SQLiteDatabase db = helper.getWritableDatabase();

        //SQL文作成
        String sql = "SELECT * FROM weightdb";
        //where date BETWEEN TO_DATE(NOW()-INTERVAL 5DAY, 'YYYY/MM/DD')
        // AND TO_DATE(NOW(), 'YYYY/MM/DD');"
        // AND TO_DATE('2017-6-30', 'YYYY/MM/DD');"
        //データ取得
        Cursor c = db.rawQuery(sql, null);

        int loop = 0;

        //Coursor型ｃに入っているデータを順番に取り出す
        while (c.moveToNext()){
　　　      //getColumnIndexの引数は列の名前
            date[loop++] = c.getString(c.getColumnIndex("date"));
            weight = c.getInt(c.getColumnIndex("weight"));
        }
    }*/

    //viewの初期化
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button button01 = view.findViewById(R.id.settingButton);
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    // BackStackを設定

                    fragmentTransaction.replace(R.id.main_container, fragment02, "Wfragment2");
                    // Fragmentを組み込む
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    //戻るボタンで戻るやつ↓
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }
}
