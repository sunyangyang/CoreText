package com.knowbox.base.service.log.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.hyena.framework.database.BaseTable;

/**
 * Created by yangzc on 17/9/20.
 */
public class LogTable extends BaseTable<LogItem> {

    public static final String LOG_TEXT = "log";

    public LogTable(SQLiteOpenHelper sqlHelper) {
        super("log", sqlHelper);
    }

    @Override
    public LogItem getItemFromCursor(Cursor cursor) {
        LogItem item = new LogItem();
        item.mId = getValue(cursor, _ID, Integer.class);
        item.mLogText = getValue(cursor, LOG_TEXT, String.class);
        return item;
    }

    @Override
    public ContentValues getContentValues(LogItem item) {
        ContentValues values = new ContentValues();
        values.put(LOG_TEXT, item.mLogText);
        return values;
    }

    @Override
    public String getCreateSql() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + getTableName() + " (" + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LOG_TEXT + " TEXT"
                + "); ";
        return sql;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        dropTable();
        execSQL(getCreateSql());
    }
}
