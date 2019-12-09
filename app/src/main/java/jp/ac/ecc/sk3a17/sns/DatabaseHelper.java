package jp.ac.ecc.sk3a17.sns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //データベースファイル名
    private static final String DATABASE_NAME = "calorie.db";

    //バージョン情報の定義
    private static final int DATABASE_VERSION = 1;

    //コンストラクタ
    public DatabaseHelper(Context context){
        //親クラスのコンストラクタ呼び出し
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //運動テーブル作成
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE exerciseList(");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("name TEXT,");
        sb.append("mets FLOAT,");
        sb.append("second FLOAT,");
        sb.append("part INTEGER);");
        String sql = sb.toString();
        //SQL実行
        db.execSQL(sql);
        //StringBuilder初期化
        sb.setLength(0);

        //日ごとの運動テーブル作成
        sb.append("CREATE TABLE day_exercise(");
        sb.append("day TEXT,");
        sb.append("exercise_id INTEGER);");
        sql = sb.toString();
        db.execSQL(sql);
        sb.setLength(0);

        //日ごとの消費カロリー
        sb.append("CREATE TABLE day_consumption(");
        sb.append("day TEXT,");
        sb.append("consumption INTEGER);");
        sql = sb.toString();
        db.execSQL(sql);


        //運動テーブルに初期データ挿入
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (1, \"プッシュアップ（ノーマル）\", 2.8, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (2, \"プッシュアップ（ワイド）\", 2.8, 1.8, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (3, \"プッシュアップ（ナロー）\", 2.8, 2, 1 );");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (4, \"プッシュアップ系（高強度なインターバル）\", 7.0, 1.7, 1 );");

        /*
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (5, \"ベンチプレス（ノーマル）\", 5.0, 3, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (6, \"ベンチプレス（ワイド）\", 5.0, 2.8, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (7, \"ベンチプレス（ナロー）\", 5.0, 3, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (8, \"ベンチプレス（インクライン）\", 5, 2.8, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (9, \"ベンチプレス（デクライン）\", 5.0, 3, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (10, \"マシンチェストプレス\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (11, \"ケーブルフライ\", 5.0, 2,1 );");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (12, \"ケーブルフライ(ハイ)\", 5.0, 2, 1 );");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (13, \"ケーブルフライ(ロー)\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (14, \"ダンベルプレス\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (15, \"ダンベルインクラインプレス\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (16, \"ダンベルリバースグリッププレス\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (17, \"ダンベルデクラインプレス\", 5.0, 2, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (18, \"ダンベルフライ\", 5.0, 2.1, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (19, \"ダンベルインクラインフライ\", 5.0, 2.1, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (20, \"ダンベルデクラインフライ\", 5.0, 2.1, 1);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (21, \"バックエクステンション\", 2.8, 1.7, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (22, \"懸垂（通常）\", 2.8, 3, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (23, \"懸垂（高強度なインターバル）\", 7.0, 2.8, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (24, \"インバーテッドロウ\", 2.8, 3, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (25, \"デッドリフト\", 5.0, 3, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (26, \"ベントオーバーローイング\", 5.0, 1.9, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (27, \"シュラッグ\", 5.0, 3, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (28, \"フロントネックプルダウン\", 5.0, 3, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (29, \"ビハインドネックプルダウン\", 5.0, 2.5, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (30, \"シーテッドローイング\", 5.0, 2.8, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (31, \"ワンハンドローイング\", 5.0, 2.2, 2);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (32, \"パイクプレス\", 2.8, 2, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (33, \"アップライトロウ\", 4, 2.2, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (34, \"サイドレイズ\", 4, 1.8, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (35, \"フロントレイズ\", 4, 2.4, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (36, \"リアレイズ\", 4.2, 2.1, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (37, \"ダンベルショルダーブレス\", 4.2, 2, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (38, \"バーベルショルダープレス\", 4.5, 2.2, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (39, \"ショルダーシュラッグ\", 5.0, 3, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (40, \"マシンショルダープレス\", 5.0, 2, 3);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (41, \"ダンベルカール\", 4.0, 2.4, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (42, \"コンセントレーションカール\", 2.8, 2.3, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (43, \"インクラインカール\", 4.0, 2.4, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (44, \"ハンマーカール\", 4.0, 2.3, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (45, \"インクラインハンマーカール\", 4.0, 2.3, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (46, \"バーベルカール\", 5.0, 2.3, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (47, \"プリーチャーズカール\", 4.0, 2.3, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (48, \"ケーブルカール\", 4.0, 2.2, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (49, \"ワンハンドトライセプスエクステンション\", 3.8, 2.8, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (50, \"トライセプスキックバック\", 3.8, 2.4, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (51, \"フレンチプレス\", 4.0, 2.4, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (52, \"リバースプッシュアップ\", 2.8, 1.8, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (53, \"プレスダウン\", 4.0, 2.2, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (54, \"リストカール\", 2.8, 2, 4);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (55, \"クランチ\", 2.8, 1.9, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (56, \"ヒップレイズ\", 2.8, 1.9, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (57, \"レッグレイズ\", 2.8, 3.0, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (58, \"ハンギングレッグレイズ\", 2.8, 3, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (59, \"ドラゴンフラッグ\", 2.8, 3.9, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (60, \"サイドベンド\", 4.0, 2.5, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (61, \"ケーブルクランチ\", 4.0, 2.5, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (62, \"バイシクルクランチ\", 2.8, 0.4, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (63, \"腹筋ローラー\", 2.8, 4.5, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (64, \"ロシアンツイストハイパー\", 2.8, 0.6, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (65, \"ツイストクランチ\", 2.8, 1.9, 5);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (66, \"スクワット(自重)\", 2.8, 2.3, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (67, \"ワンレッグスクワット\", 2.8, 3, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (68, \"フルスクワット(バーベル)\", 5.0, 3.6, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (69, \"パラレルスクワット\", 5.0, 3.4, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (70, \"ハーフスクワット\", 5.0, 3.2, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (71, \"クォータースクワット\", 5.0, 3.0, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (72, \"フロントスクワット\", 5.0, 3.4, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (73, \"ダンベルスクワット\", 4.0, 3.4, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (74, \"ランジスクワット\", 5.0, 3, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (75, \"ブルガリアンスクワット\", 5.0, 2.1, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (76, \"レッグプレス\", 5.0, 3, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (77, \"レッグエクステンション\", 5.0, 2.4, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (78, \"レッグカール\", 5.0, 2.4, 6);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (79, \"サーキットトレーニング(ほどほど)\" , 4.3, null, 7);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (80, \"サーキットトレーニング(高強度)\" , 8.0, null, 7);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (81, \"ストレッチ・ヨガ\" , 2.5, null, 7);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (82, \"ハンドボール\" , 6.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (83, \"バドミントン\" , 4.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (84, \"バスケットボール\" , 7.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (85, \"ゴルフ\" , 3.8, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (86, \"ボクシング：スパーリング\" , 6.8, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (87, \"カーリング\" , 3.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (88, \"ボウリング\" , 2.0, nll, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (89, \"ビリヤード\" , 1.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (90, \"柔道、柔術、空手\", 9.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (91, \"ダーツ\" , 1.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (92, \"ホッケー\" , 6.8, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (93, \"乗馬\" , 4.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (94, \"ラクロス\" , 7.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (95, \"ロッククライミング、または山登り\" , 6.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (96, \"縄跳び\" , 10.8, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (97, \"ラグビー\" , 7.3, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (98, \"スケートボード\" , 4.5, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (99, \"サッカー\" , 9.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (100, \"ソフトボール、または野球：投球\" , 4.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (101, \"卓球\" , 3.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (102, \"テニス\" , 6.3, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (103, \"バレーボール\" , 5.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (104, \"陸上競技(投擲)\" , 3.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (105, \"陸上競技(跳躍)\" , 5.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (106, \"陸上競技(クロスカントリー)\" , 8.0, null, 8);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (107, \"カヌー、ボートを漕ぐ\" , 3.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (108, \"スキンダイビング、スキューバダイビング\" , 6.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (109, \"シュノーケリング\" , 4.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (110, \"サーフィン\" , 3.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (111, \"水泳：競技、トレーニング\" , 9.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (112, \"水泳：のんびりと泳ぐ\" , 5.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (113, \"水球\" , 9.0, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (114, \"水中歩行：楽な労力、ゆっくり\" , 1.5, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (115, \"水中歩行：ほどほどの労力\" , 3.5, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (116, \"水中歩行：きつい労力\" , 5.8, null, 9);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (117, \"アイスダンス\" , 13.0, null, 10);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (118, \"スピードスケート\" , 12.3, null, 10);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (119, \"スキー：全般\" , 6.0, null, 10);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (120, \"スキージャンプ\" , 6.0, null, 10);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (121, \"バレエ：モダン、ジャズ、全般\" , 4.0, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (122, \"エアロビックダンス：低強度\" , 4.5, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (123, \"エアロビックダンス：高強度\" , 8.5, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (124, \"民族舞踊や伝統舞踊\" , 3.5, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (125, \"一般的なダンス(例：ディスコ、フォークなど）\" , 6.8, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (126, \"社交ダンス：速い\" , 4.5, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (127, \"社交ダンス：ゆっくり\" , 2.0, null, 11);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (128, \"歩行：3.2km/時未満、とてもゆっくり、散歩、水平な地面\" , 1.0, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (129, \"歩行：3.2km/時、ゆっくり、平らで固い地面\" , 1.8, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (130, \"歩行：4.5km/以上、ほどほどの速さ、平らで固い地面\" , 2.8, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (131, \"歩行：上り坂、1-5%の傾斜\" , 4.3, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (132, \"歩行：上り坂、6-15%の傾斜\" , 7.0, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (133, \"階段を上る：ゆっくり\" , 3.0, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (134, \"階段を上る：速い\" , 7.8, null, 12);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (135, \"ランニング：6-8km\" , 6.0, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (136, \"ランニング：8-10km\" , 8.3, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (137, \"ランニング：10-12km\" ,9.9 , null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (138, \"ランニング：12-14km\" , 10.8, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (139, \"ランニング：14-15km\" , 11.4, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (140, \"ランニング：15-17km\" , 13.5, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (141, \"ランニング：18km前後\" , 15.3, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (142, \"ランニング：19km前後\" , 17.7, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (143, \"ランニング：20km前後\" , 18.1, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (144, \"ランニング：21km前後\" , 19.2, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (145, \"ランニング：22km前後\" , 21.5, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (146, \"自転車に乗る：レジャー、通勤、娯楽\" , 3.0, null, 13);");
        db.execSQL("INSERT INTO exerciseList(_id,name,mets,second,part) values (147, \"階段ダッシュ \" , 14, null, 13);");



         */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

}
