package com.applicforge.umbalarm.di

import android.content.Context
import com.applicforge.umbalarm.manager.FcmManager
import com.applicforge.umbalarm.manager.PreferencesManager
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