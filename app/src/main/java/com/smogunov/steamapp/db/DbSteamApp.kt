package com.smogunov.steamapp.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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
    val testU: Long
)

@Entity("news")
data class DbNew(
    @PrimaryKey
    val id: Int,
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

    @Query("SELECT * FROM steam_apps LIMIT :limit OFFSET :offset")
    suspend fun getSteamApps(limit: Int, offset: Int): List<DbSteamApp>

    @Query("SELECT * FROM steam_apps LIMIT :limit OFFSET :offset")
    fun getSteamAppsPaging(limit: Int, offset: Int): PagingSource<Int, DbSteamApp>

    @Query("SELECT * FROM steam_apps WHERE name LIKE :query")
    fun getAllSteamAppsFlow(query: String): PagingSource<Int, DbSteamApp>

    @Query("SELECT * FROM steam_apps WHERE name LIKE :query")
    fun getAllSteamAppsFlowList(query: String): Flow<List<DbSteamApp>>

    @Query("SELECT * FROM steam_apps WHERE name LIKE :query")
    fun getAllSteamAppsList(query: String): List<DbSteamApp>

    @Insert
    suspend fun insertSteamApps(apps: List<DbSteamApp>)

    @Query("DELETE FROM steam_apps")
    suspend fun clearSteamApps()

    @Query("SELECT * FROM news WHERE appid = :appid")
    fun getNewsFlow(appid: UInt): Flow<List<DbNew>>
}

@Database(entities = [DbSteamApp::class, DbNew::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class SteamAppDatabase : RoomDatabase(){
    abstract fun streamAppDao(): SteamAppDao

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