package com.smsforw.di

import android.content.Context
import androidx.room.Room
import com.smsforw.data.local.db.AppDatabase
import com.smsforw.data.local.db.dao.ForwardingRuleDao
import com.smsforw.data.local.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smsforw.db"
        ).build()

    @Provides
    fun provideRuleDao(db: AppDatabase): ForwardingRuleDao = db.forwardingRuleDao()
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
        SettingsDataStore(context)
}
