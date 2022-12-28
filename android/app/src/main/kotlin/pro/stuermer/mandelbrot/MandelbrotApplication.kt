package pro.stuermer.mandelbrot

import android.app.Application
import timber.log.Timber
import timber.log.Timber.Forest.plant
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MandelbrotApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        // Dependency injection
        startKoin {
            // Koin Android logger
            androidLogger(
                if (BuildConfig.DEBUG) {
                    Level.ERROR
                } else {
                    Level.NONE
                }
            )

            // inject Android context
            androidContext(applicationContext)

            modules(listOf(appModule))
        }
    }
}
