package com.zareshahi.myreport

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.zareshahi.myreport.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zareshahi.myreport.database.dao.NoteDao
import com.zareshahi.myreport.database.repository.NoteRepository
import com.zareshahi.myreport.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

class MyReportApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val viewModeModule = module {
            viewModel{
                HomeViewModel(get())
            }
        }

        val singleModule = module {
            single {
                Room.databaseBuilder(
                    context = applicationContext,
                    klass = AppDatabase::class.java,
                    name = "com.zareshahi.myreport.database"
                ).build()
            }
            single {
                val db =get<AppDatabase>()
                db.noteDao()
            }
            single {
                NoteRepository(get())
            }
        }
        startKoin {
            androidContext(this@MyReportApplication)
            modules(listOf(singleModule,viewModeModule))
        }

    }
}