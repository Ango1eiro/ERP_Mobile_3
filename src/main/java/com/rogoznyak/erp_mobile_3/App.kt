package com.rogoznyak.erp_mobile_3

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.rogoznyak.erp_mobile_3.network.MyCredentials
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.search.SearchFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton


class MyCustomApplication() : Application() {

    companion object {
        lateinit var mAppComponent : AppComponent
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        mAppComponent = DaggerAppComponent.builder()
            // list of modules that are part of this component need to be created here too
            .appModule(AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
            .netModule(NetModule())
            .build();
        // Required initialization logic here!
    }

}

@Module
class AppModule (val mApplication: Application ) {

    @Provides
    @Singleton
    fun providesApplication() = mApplication

    @Provides
    @Singleton
    fun provideAppContext() = mApplication.applicationContext
}

@Module
class NetModule {
    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    fun  // Application reference must come from AppModule.class
            providesSharedPreferences(application: Application?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}

@Component(modules=[AppModule::class, NetModule::class])
@Singleton
interface AppComponent {
    fun inject(myCredentials: MyCredentials)
    fun inject(repository: TodoRepository)
}


