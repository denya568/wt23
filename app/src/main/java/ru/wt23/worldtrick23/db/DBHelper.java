package ru.wt23.worldtrick23.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.wt23.worldtrick23.R;
import ru.wt23.worldtrick23.io.Me;

public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION = 5;
    private static final String NAME_OF_DB = "users";
    public static final String TABLE_USER = "user";

    public static final String ID = "_id";
    public static final String SERVER_ID = "server_id";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";

    public static final String SURNAME = "surname";
    public static final String PATRONYMIC = "patronymic";
    public static final String EMAIL = "email";
    public static final String DATE_OLD = "date_old";
    public static final String INSTAGRAM = "instagram";
    public static final String ABOUTH = "abouth";
    public static final String DATE_REG = "date_reg";
    public static final String ACTIVE = "active";
    public static final String POSTMAIL = "post_mail";

    public static final String RANG = "rang";
    public static final String WINS = "wins";
    public static final String FAILS = "fails";
    public static final String COUNT_BATTLES = "count_battles";

    public static final String TRICKING = "tricking";
    public static final String PARKOUR = "parkour";
    public static final String BREAK = "break";
    public static final String TRAMPOLINE = "trampoline";

    public static final String URL = "https://wt23.ru/api/";

    public DBHelper(Context context) {
        super(context, NAME_OF_DB, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER + " (" + ID + " integer primary key, " + SERVER_ID + " integer not null, " + LOGIN + " text not null, " + PASSWORD + " text not null, " + NAME + " text not null, "
                + WINS + " integer, " + FAILS + " integer, " + RANG + " integer, " + COUNT_BATTLES + " integer, "
                + SURNAME + " text, " + PATRONYMIC + " text, " + EMAIL + " text not null, " + DATE_OLD + " text, " + INSTAGRAM + " text, " + ABOUTH + " text, " + DATE_REG + " text, "
                + ACTIVE + " text, " + TRICKING + " text, " + PARKOUR + " text, " + BREAK + " text, " + TRAMPOLINE + " text, " + POSTMAIL + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_USER + ";");
        onCreate(db);
    }

    public static void createUser(Context context, Me me) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SERVER_ID, me.getId());
        contentValues.put(LOGIN, me.getLogin());
        contentValues.put(PASSWORD, me.getPassword());
        contentValues.put(NAME, me.getName());
        contentValues.put(NAME, me.getName());
        contentValues.put(SURNAME, me.getSurname());
        contentValues.put(PATRONYMIC, me.getPatronymic());
        contentValues.put(EMAIL, me.getEmail());
        contentValues.put(DATE_OLD, me.getDateOld());
        contentValues.put(INSTAGRAM, me.getInstagram());
        contentValues.put(ABOUTH, me.getAbouth());
        contentValues.put(DATE_REG, me.getDateReg());
        contentValues.put(RANG, me.getRang());
        contentValues.put(WINS, me.getWins());
        contentValues.put(FAILS, me.getFails());
        contentValues.put(COUNT_BATTLES, me.getCountBattles());
        contentValues.put(ACTIVE, me.getActive());
        contentValues.put(TRICKING, me.getTricking());
        contentValues.put(TRAMPOLINE, me.getTrampoline());
        contentValues.put(BREAK, me.getBreak());
        contentValues.put(PARKOUR, me.getParkour());
        contentValues.put(POSTMAIL, me.getPostMail());

        database.insert(TABLE_USER, null, contentValues);
        database.close();
        dbHelper.close();
    }

    public static void updateUser(Context context, UserDB userDB) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERVER_ID, userDB.getServerId());
        contentValues.put(LOGIN, userDB.getLogin());
        contentValues.put(PASSWORD, userDB.getPassword());
        contentValues.put(NAME, userDB.getName());
        contentValues.put(SURNAME, userDB.getSurname());
        contentValues.put(PATRONYMIC, userDB.getPatronymic());
        contentValues.put(EMAIL, userDB.getEmail());
        contentValues.put(DATE_OLD, userDB.getDateOld());
        contentValues.put(INSTAGRAM, userDB.getInstagram());
        contentValues.put(ABOUTH, userDB.getAbouth());
        contentValues.put(DATE_REG, userDB.getDateReg());
        contentValues.put(ACTIVE, userDB.getActive());
        contentValues.put(RANG, userDB.getRang());
        contentValues.put(WINS, userDB.getWins());
        contentValues.put(FAILS, userDB.getFails());
        contentValues.put(COUNT_BATTLES, userDB.getCountBattles());
        contentValues.put(TRICKING, userDB.getTricking());
        contentValues.put(PARKOUR, userDB.getParkour());
        contentValues.put(BREAK, userDB.getBreak());
        contentValues.put(TRAMPOLINE, userDB.getTrampoline());
        contentValues.put(POSTMAIL, userDB.getPostMail());
        database.update(TABLE_USER, contentValues, ID + "=" + userDB.getPhoneId(), null);
        database.close();
        dbHelper.close();
    }

    public static UserDB getUser(Context context) {
        UserDB userDB = null;
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_USER, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(LOGIN)) != null) {
                userDB = new UserDB();
                userDB.setServerId(cursor.getInt(cursor.getColumnIndex(SERVER_ID)));
                userDB.setPhoneId(cursor.getInt(cursor.getColumnIndex(ID)));
                userDB.setLogin(cursor.getString(cursor.getColumnIndex(LOGIN)));
                userDB.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
                userDB.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                userDB.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME)));
                userDB.setPatronymic(cursor.getString(cursor.getColumnIndex(PATRONYMIC)));
                userDB.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                userDB.setDateOld(cursor.getString(cursor.getColumnIndex(DATE_OLD)));
                userDB.setInstagram(cursor.getString(cursor.getColumnIndex(INSTAGRAM)));
                userDB.setAbouth(cursor.getString(cursor.getColumnIndex(ABOUTH)));
                userDB.setDateReg(cursor.getString(cursor.getColumnIndex(DATE_REG)));
                userDB.setRang(cursor.getInt(cursor.getColumnIndex(RANG)));
                userDB.setWins(cursor.getInt(cursor.getColumnIndex(WINS)));
                userDB.setFails(cursor.getInt(cursor.getColumnIndex(FAILS)));
                userDB.setCountBattles(cursor.getInt(cursor.getColumnIndex(COUNT_BATTLES)));
                userDB.setActive(cursor.getString(cursor.getColumnIndex(ACTIVE)));
                userDB.setTricking(cursor.getString(cursor.getColumnIndex(TRICKING)));
                userDB.setTrampoline(cursor.getString(cursor.getColumnIndex(TRAMPOLINE)));
                userDB.setBreak(cursor.getString(cursor.getColumnIndex(BREAK)));
                userDB.setParkour(cursor.getString(cursor.getColumnIndex(PARKOUR)));
                userDB.setPostMail(cursor.getString(cursor.getColumnIndex(POSTMAIL)));
            } else {
                break;
            }
        }
        cursor.close();
        database.close();
        dbHelper.close();
        return userDB;
    }

    public static void deleteUser(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TABLE_USER, null, null);
        database.close();
        dbHelper.close();
    }

    public static void setBattleRequestsCount(Context context, int reqs) {
        SharedPreferences sp = context.getSharedPreferences("BattleRequests", Context.MODE_PRIVATE);
        sp.edit().putInt("count", reqs).apply();
    }

    public static boolean getShopSaveChanges(Context context) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        return sp.getBoolean("shopSaveChanges", false);
    }

    public static void setShopSaveChanges(Context context, boolean save) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        sp.edit().putBoolean("shopSaveChanges", save).apply();
    }

    public static boolean getNewsShowStreams(Context context) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        return sp.getBoolean("showStreams", false);
    }

    public static void setNewsShowStreams(Context context, boolean save) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        sp.edit().putBoolean("showStreams", save).apply();
    }

    public static int getBattleRequestsCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("BattleRequests", Context.MODE_PRIVATE);
        if (getUser(context) != null) {
            return (sp.getInt("count", 0));
        } else {
            return 0;
        }
    }

    public static void setLastSesh(Context context, int nav_item) {
        SharedPreferences sp = context.getSharedPreferences("LastSesh", Context.MODE_PRIVATE);
        sp.edit().putInt("item", nav_item).apply();
    }

    public static int getLastSesh(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LastSesh", Context.MODE_PRIVATE);
        if (getUser(context) != null) {
            return (sp.getInt("item", R.id.nav_news));
        } else {
            return R.id.nav_news;
        }
    }

    public static int getNewsCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        return sp.getInt("newsCount", 5);
    }

    public static void setNewsCount(Context context, int count) {
        SharedPreferences sp = context.getSharedPreferences("content", Context.MODE_PRIVATE);
        sp.edit().putInt("newsCount", count).apply();
    }

}
