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

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "tb_app",
    indices = [Index(value = ["package_name"], orders = [Index.Order.ASC], name = "idx_packageName", unique = true)]
)
data class App(
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    val icon: String?,
    val version: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface AppDao {
    @Query("SELECT * FROM tb_app ORDER BY id DESC LIMIT 10")
    fun getApps(): Flow<List<App>>

    @Insert
    suspend fun insertApp(item: App)
}
