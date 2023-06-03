package com.smogunov.steamapp.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date


class DateConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
}

//class DateConterter {
//    @TypeConverter
//    fun dateToInt(aDate: Date): Int = aDate.time.toInt()
//
//    @TypeConverter
//    fun intToDate(aInt: Int): Date = Date(aInt.toLong())
//}

@Entity("steam_apps")
data class DbSteamApp(
    @PrimaryKey
//    val id: Int = 0,
    val appid: Int,
    val name: String,
    var loadedNews: Boolean
)

@Entity("news")
data class DbNew(
    @PrimaryKey
//    val id: Int,
    val gid: Int,
    val appid: Int,
    val title: String,
    val url: String,
    val is_external_url: Boolean,
    val author: String,
    val contents: String,
    val date: Date
)

@Dao
interface SteamAppDao{
    @Update
    suspend fun updateSteamApp(steamApp: DbSteamApp)
    @Query("SELECT * FROM steam_apps WHERE name LIKE :filter ORDER BY appid DESC")
    suspend fun getAllSteamAppsSuspend(filter: String): List<DbSteamApp>
    @Insert
    suspend fun insertSteamApps(apps: List<DbSteamApp>)
    @Query("DELETE FROM steam_apps")
    suspend fun clearSteamApps()

    @Query("SELECT * FROM steam_apps WHERE appid = :appid")
    suspend fun getSteamApp(appid: Int): DbSteamApp


    @Query("SELECT * FROM news WHERE appid = :appid ORDER BY gid DESC")
    suspend fun getAllNewsSuspend(appid: Int): List<DbNew>
    @Query("DELETE FROM news WHERE appid = :appid")
    suspend fun clearNews(appid: Int)
    @Insert
    suspend fun insertNews(news: List<DbNew>)

    @Query("SELECT contents FROM news WHERE gid = :gid")
    suspend fun getContents(gid: Int): String
}

@Database(entities = [DbSteamApp::class, DbNew::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class SteamAppDatabase : RoomDatabase(){
    abstract fun steamAppDao(): SteamAppDao

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