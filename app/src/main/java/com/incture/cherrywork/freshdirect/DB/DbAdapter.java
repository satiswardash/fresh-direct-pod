package com.incture.cherrywork.freshdirect.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by harshu on 12/16/2015.
 */
public class DbAdapter {

    private static SQLiteDatabase sqliteDB;
    private static DbAdapter dbAdapter;

    Context ctx;

    public static DbAdapter getDbAdapterInstance()
    {
        if (null == dbAdapter)
        {
            dbAdapter = new DbAdapter();
        }
        return dbAdapter;
    }

    public DbAdapter open(Context ctx)
    {
        DBHelper dbHelperTasks= new DBHelper(ctx);
        sqliteDB =  dbHelperTasks.getWritableDatabase();
        dbHelperTasks.onCreate(sqliteDB);
        return this;
    }

    public Cursor getResultSet(String tableName, String[] columns,
                               String orderBy,Context c) throws SQLException {
        Cursor mCursor;
        if(sqliteDB == null){
            DbAdapter.getDbAdapterInstance().open(c);
        }
        mCursor = sqliteDB.query(true, tableName, columns, null, null, null,
                null, orderBy, null);

        return mCursor;
    }





    public Cursor getResultSet(String tableName, String[] columns,
                               String orderBy,String where,Context c) throws SQLException {
        Cursor mCursor = null;
        if (sqliteDB== null) {
            DbAdapter.getDbAdapterInstance().open(c);
            return mCursor;
        }
        mCursor = sqliteDB.query(true, tableName, columns, where, null, null,
                null, orderBy, null);

        return mCursor;

    }

    public int delete(String tableName, String whereClause, String[] whereArgs) {

        return sqliteDB.delete(tableName, whereClause, whereArgs);
    }

    public long insert(String tableName, ContentValues contentValues) {
        return sqliteDB.insert(tableName, null, contentValues);
    }
    public int update(String tableName,ContentValues contentValues, String whereClause) {
        return sqliteDB.update(tableName, contentValues, whereClause,null);
    }

    public boolean delete(String tableName)
    {
        return sqliteDB.delete(tableName, null, null) > 0;
    }


    public void insert(String tablename,String s,ContentValues values)
    {
        long row=sqliteDB.insert(tablename,s,values);
    }

    public Cursor rawQuery(String query, String s)
    {
        Cursor cursor=sqliteDB.rawQuery(query, null);
        return cursor;
    }

    public boolean isDataBaseOpen()
    {
        boolean isDBOpen = false;

        if(null != sqliteDB)
        {
            isDBOpen = sqliteDB.isOpen();
        }
        return isDBOpen;
    }

    public Cursor getResultSet(String tableName, String[] columns,
                               String whereClause, String groupBy, String orderBy,Context c) {
        Cursor mCursor = null;
        if (sqliteDB== null) {
            DbAdapter.getDbAdapterInstance().open(c);
            return mCursor;
        }
        return sqliteDB.query(true, tableName, columns, whereClause, null, groupBy,
                null, orderBy, null);
    }
}
