package jp.ac.ecc.sk3a17.sns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //データベースファイル名
    private static final String DATABASE_NAME = "weightDb.db";

    //バージョン情報の定義
    private static final int DATABASE_VERSION = 1;

    //コンストラクタ
    public DatabaseHelper(Context context){
        //親クラスのコンストラクタ呼び出し
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //テーブル作成用SQL文の作成
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE weightDb(");
        sb.append("date String,");
        sb.append("weight INTEGER,");
        sb.append(");");
        String sql = sb.toString();

        //SQL実行
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

}
