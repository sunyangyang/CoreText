package com.knowbox.base.utils;

import android.content.Context;

import com.hyena.framework.database.BaseDataBaseHelper;
import com.hyena.framework.database.DataBaseHelper;
import com.knowbox.base.service.log.db.LogTable;

/**
 * Created by yangzc on 17/9/26.
 */
public abstract class BoxDBHelper extends BaseDataBaseHelper {

    public BoxDBHelper(Context context, String name, int version) {
        super(context, name, version);
    }

    @Override
    public void initTables(DataBaseHelper db) {
        super.initTables(db);
        addTable(LogTable.class, new LogTable(db));
    }

//    LogTable logTable = getTable(LogTable.class);
//    logTable.onUpgrade(db, oldVersion, newVersion);
}
