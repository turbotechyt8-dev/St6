package com.strengthtracker

import android.app.Application
import android.content.Context
import com.strengthtracker.data.StrengthDatabase
import com.strengthtracker.data.StrengthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StrengthApp : Application() {

    val repository: StrengthRepository by lazy {
        StrengthRepository(StrengthDatabase.get(this).dao())
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { repository.ensureDefaultSchedule() }
    }
}

/** Gives any screen access to the shared repository. */
fun Context.repository(): StrengthRepository =
    (applicationContext as StrengthApp).repository
