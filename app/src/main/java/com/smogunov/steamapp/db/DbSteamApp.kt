package com.smogunov.steamapp.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity("steam_apps")
data class DbSteamApp(
    @PrimaryKey
//    val id: Int = 0,
    val appid: Int,
    val name: String
)

@Dao
interface StreamAppDao{

    @Query("SELECT * FROM steam_apps LIMIT :limit OFFSET :offset")
    suspend fun getSteamApps(limit: Int, offset: Int): List<DbSteamApp>

    @Insert
    suspend fun insertSteamApps(apps: List<DbSteamApp>)

    @Query("DELETE FROM steam_apps")
    suspend fun clearSteamApps()
}

@Database(entities = [DbSteamApp::class], version = 1, exportSchema = false)
abstract class SteamAppDatabase : RoomDatabase(){
    abstract fun streamAppDao(): StreamAppDao

    companion object {
        private var INSTANCE: SteamAppDatabase? = null
        const val DB_NAME = "steam_app.db"

        fun getInstance(context: Context): SteamAppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context, SteamAppDatabase::class.java, DB_NAME)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}