package com.github.stonybean.mygraph

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Created by Joo on 2021/09/03
 */
class GraphApplication: Application() {
    private val appModule = module {
        viewModel { ViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GraphApplication)
            modules(appModule)
        }
    }
}