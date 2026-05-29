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

package com.iceberry.notifybox.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iceberry.notifybox.data.local.di.LocalDateTimeConverter

@Database(
    entities = [Notify::class, Device::class],
    version = 5
)
@TypeConverters(value = [LocalDateTimeConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun notifyDao(): NotifyDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `devices` (`endpointId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `lastSeen` INTEGER NOT NULL, PRIMARY KEY(`endpointId`))"
                )
                db.execSQL("DROP TABLE IF EXISTS tb_app")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `devices_new` (`deviceUniqueId` TEXT NOT NULL, `endpointId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `lastSeen` INTEGER NOT NULL, PRIMARY KEY(`deviceUniqueId`))"
                )
                db.execSQL(
                    "INSERT INTO `devices_new` (deviceUniqueId, endpointId, name, description, lastSeen) SELECT endpointId, endpointId, name, description, lastSeen FROM devices"
                )
                db.execSQL("DROP TABLE `devices`")
                db.execSQL("ALTER TABLE `devices_new` RENAME TO `devices`")
            }
        }
    }
}
