package com.example.luxres; // Use your correct package name

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main database class for the application.
 * Includes User and Booking tables.
 */
// --- Increment version number ---
@Database(entities = {UserEntity.class, BookingEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract BookingDao bookingDao(); // <<< Add abstract method for BookingDao

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "luxevista_database")
                            // --- Add Migration if upgrading from version 1 ---
                            // .addMigrations(MIGRATION_1_2) // Add migration from v1 to v2
                            // --- OR allow destructive migration during development ---
                            .fallbackToDestructiveMigration() // Wipes DB on version change - OK for dev ONLY
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- Define Migration from version 1 to 2 (Adds bookings table) ---
    // static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    //     @Override
    //     public void migrate(@NonNull SupportSQLiteDatabase database) {
    //         Log.i("DB_MIGRATION", "Migrating database from version 1 to 2");
    //         // SQL command to create the new bookings table
    //         database.execSQL("CREATE TABLE IF NOT EXISTS `bookings` (`booking_id` TEXT NOT NULL, `user_id` INTEGER NOT NULL, `item_id` TEXT NOT NULL, `item_name` TEXT, `booking_type` TEXT, `start_date` TEXT, `end_date` TEXT, `total_price` REAL NOT NULL, `status` TEXT, PRIMARY KEY(`booking_id`), FOREIGN KEY(`user_id`) REFERENCES `users`(`uid`) ON UPDATE NO ACTION ON DELETE CASCADE )");
    //         // SQL command to create index on user_id
    //         database.execSQL("CREATE INDEX IF NOT EXISTS `index_bookings_user_id` ON `bookings` (`user_id`)");
    //         Log.i("DB_MIGRATION", "Bookings table created.");
    //     }
    // };
    // --- End Migration ---

}
