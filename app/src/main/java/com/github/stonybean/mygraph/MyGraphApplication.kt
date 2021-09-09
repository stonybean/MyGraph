package com.github.stonybean.mygraph

import android.app.Application
import com.github.stonybean.mygraph.viewmodel.GraphViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Created by Joo on 2021/09/09
 */
class MyGraphApplication: Application() {
    private val viewModelModule = module {
        viewModel { GraphViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyGraphApplication)
            modules(viewModelModule)
        }
    }
}