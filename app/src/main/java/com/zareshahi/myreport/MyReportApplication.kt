package com.zareshahi.myreport

import android.app.Application
import androidx.room.Room
import com.zareshahi.myreport.database.AppDatabase
import com.zareshahi.myreport.database.repository.CategoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.zareshahi.myreport.database.repository.NoteRepository
import com.zareshahi.myreport.screen.AddNewReportViewModel
import com.zareshahi.myreport.screen.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

class MyReportApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val viewModeModule = module {
            viewModel{
                HomeViewModel(get(),get())
            }
            viewModel{
                AddNewReportViewModel(get(),get())
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
            single {
                val db =get<AppDatabase>()
                db.categoryDao()
            }
            single {
                CategoryRepository(get())
            }
        }
        startKoin {
            androidContext(this@MyReportApplication)
            modules(listOf(singleModule,viewModeModule))
        }

    }
}