package com.jigar.core.data.module

import android.content.Context
import com.jigar.core.rootservice.service.RemoteRootService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteRootServiceModule {
    @Provides
    @Singleton
    fun provideService(@ApplicationContext context: Context): RemoteRootService = RemoteRootService(context)
}
