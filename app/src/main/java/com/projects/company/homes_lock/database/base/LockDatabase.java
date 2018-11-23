package com.projects.company.homes_lock.database.base;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.projects.company.homes_lock.database.daos.DeviceDao;
import com.projects.company.homes_lock.database.daos.DeviceErrorDao;
import com.projects.company.homes_lock.database.daos.ErrorDao;
import com.projects.company.homes_lock.database.daos.UserDao;
import com.projects.company.homes_lock.database.daos.UserLockDao;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.DeviceError;
import com.projects.company.homes_lock.database.tables.Error;
import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;

@Database(entities = {
        Device.class,
        DeviceError.class,
        Error.class, User.class,
        UserLock.class},
        version = 1, exportSchema = false)
public abstract class LockDatabase extends RoomDatabase {

    //region declare Objects
    private static LockDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static LockDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LockDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LockDatabase.class, "lockDataBase")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract DeviceDao deviceDao();

    public abstract UserDao userDao();

    public abstract DeviceErrorDao deviceErrorDao();

    public abstract ErrorDao errorDao();

    public abstract UserLockDao userLockDao();
    //endregion declare Objects
}