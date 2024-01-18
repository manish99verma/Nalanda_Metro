package com.manish.nalandametro.di

import android.content.Context
import android.content.SharedPreferences
import com.manish.nalandametro.data.pref.PrefManager
import com.manish.nalandametro.data.repository.MetroRepository
import com.manish.nalandametro.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun providesRepositoryModule(): Repository {
        return MetroRepository()
    }

    @Singleton
    @Provides
    fun providesPrefManager(@ApplicationContext context: Context) = PrefManager(context.getSharedPreferences("my_default_pref",Context.MODE_PRIVATE))
}