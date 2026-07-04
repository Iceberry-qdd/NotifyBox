/*
 * Copyright (c) 2025 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox.data.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverter
import com.iceberry.notifybox.data.local.database.AppDatabase
import com.iceberry.notifybox.data.local.database.DeviceDao
import com.iceberry.notifybox.data.local.database.NotifyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideNotifyDao(appDatabase: AppDatabase): NotifyDao {
        return appDatabase.notifyDao()
    }

    @Provides
    fun provideDeviceDao(appDatabase: AppDatabase): DeviceDao {
        return appDatabase.deviceDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Notifies.db"
        ).addMigrations(AppDatabase.MIGRATION_3_4, AppDatabase.MIGRATION_4_5)
            .build()
    }
}

class LocalDateTimeConverter {
    @TypeConverter
    fun longToLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    }

    @TypeConverter
    fun localDateTimeToLong(localDateTime: LocalDateTime): Long {
        return localDateTime
            .toInstant(ZoneOffset.systemDefault().rules.getOffset(localDateTime))
            .toEpochMilli()
    }
}
