package com.recovery.back.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.recovery.back.data.local.dao.AppDao
import com.recovery.back.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        DailyLogEntity::class,
        WeightLogEntity::class,
        ExerciseSessionEntity::class,
        MealLogEntity::class,
        WaterLogEntity::class,
        SupplementLogEntity::class,
        AlarmConfigEntity::class,
        PainThresholdEventEntity::class,
        CustomExerciseEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Example migration for future use. Crucial to NOT use fallbackToDestructiveMigration
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Future alterations will go here
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "back_recovery_database"
                )
                .addMigrations(MIGRATION_1_2) 
                // .fallbackToDestructiveMigration() // DELIBERATELY OMITTED as per requirements
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
