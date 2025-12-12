package com.imp.fluffymood.di

import com.imp.data.repository.AnalysisRepositoryImpl
import com.imp.data.repository.ChatRepositoryImpl
import com.imp.data.repository.DailyLifePatternRepositoryImpl
import com.imp.data.repository.HomeRepositoryImpl
import com.imp.data.repository.LogRepositoryImpl
import com.imp.data.repository.MemberRepositoryImpl
import com.imp.domain.repository.AnalysisRepository
import com.imp.domain.repository.ChatRepository
import com.imp.domain.repository.DailyLifePatternRepository
import com.imp.domain.repository.HomeRepository
import com.imp.domain.repository.LogRepository
import com.imp.domain.repository.MemberRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMemberRepository(): MemberRepository = MemberRepositoryImpl()

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository = HomeRepositoryImpl()

    @Provides
    @Singleton
    fun provideLogRepository(): LogRepository = LogRepositoryImpl()

    @Provides
    @Singleton
    fun provideAnalysisRepository(): AnalysisRepository = AnalysisRepositoryImpl()

    @Provides
    @Singleton
    fun provideChatRepository(): ChatRepository = ChatRepositoryImpl()

    @Provides
    @Singleton
    fun provideDailyLifePatternRepository(): DailyLifePatternRepository = DailyLifePatternRepositoryImpl()
}
