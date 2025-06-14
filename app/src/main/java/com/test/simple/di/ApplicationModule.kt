package com.test.simple.di

import android.content.Context
import com.test.simple.manager.FcmManager
import com.test.simple.manager.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
    
    @Provides
    @Singleton
    fun provideFcmManager(
        @ApplicationContext context: Context,
        preferencesManager: PreferencesManager
    ): FcmManager {
        return FcmManager(context, preferencesManager)
    }
} 