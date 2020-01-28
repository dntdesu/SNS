package jp.ac.ecc.sk3a17.sns.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import jp.ac.ecc.sk3a17.sns.DatabaseHelper;
import jp.ac.ecc.sk3a17.sns.R;

public class Wfragment2 extends Fragment {

    public static Wfragment2 newInstance() {
        return new Wfragment2();
    }

    // FragmentのViewを生成して返す
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View wf = inflater.inflate(R.layout.fragmwnt2, container, false);

        // 画面回転時にFragmentの再生成を禁止
        setRetainInstance(true);

        return wf;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button bu1 = (Button) getActivity().findViewById(R.id.button2);
        bu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ked = (EditText) getActivity().findViewById(R.id.editText);
                EditText mted = (EditText) getActivity().findViewById(R.id.editText2);

                //入力ボタン押したらキーボードを消す
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

//
//                //今は月単位の指定しかできない
//                //入力値を文字列から数値へ　数値から文字列へして表示
                String kikan = ked.getText().toString();
                String mtai = mted.getText().toString();


                Bundle bundle = new Bundle();
                bundle.putString("mokuki", kikan);
                bundle.putString("mokutai", mtai);


                // フラグメント1に渡すためにBundleにセット
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WeightFragment frag1ment = new WeightFragment();
                //注文ふらぐめんと
                frag1ment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_container, frag1ment);
                fragmentTransaction.commit();
            }
        });
    }

    //目標データ保存メソッド
    public void saveGoal() {
        //データベースへの接続取得
        DatabaseHelper helper = new DatabaseHelper(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
    }

}
