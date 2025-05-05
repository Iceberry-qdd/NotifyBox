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

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.iceberry.notifybox.ui.statistic.PieChartDataVO
import com.iceberry.notifybox.util.IconUtils
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Fts4(
    tokenizer = FtsOptions.TOKENIZER_ICU,
    notIndexed = ["small_icon", "large_icon", "notify_id", "channel", "priority", "created_time", "deleted_time", "count"]
)
@Entity(tableName = "tb_notify")
data class Notify(
    val title: String?,
    val content: String?,
    @ColumnInfo(name = "package_name", index = true)
    val packageName: String,
    @ColumnInfo(name = "display_name", index = true)
    val displayName: String? = null,
    @ColumnInfo(name = "small_icon")
    val smallIcon: String?, // means filename
    @ColumnInfo(name = "large_icon")
    val largeIcon: String?, // means filename
    @ColumnInfo(name = "notify_id", index = true)
    val notifyId: Int,
    @ColumnInfo(name = "channel")
    val channel: String?,
    @ColumnInfo(name = "priority")
    val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    @ColumnInfo(name = "created_time", index = true)
    val createdTime: LocalDateTime,
    @ColumnInfo(name = "deleted_time")
    val deletedTime: LocalDateTime? = null,
    val count: Int = 1 // No sense but stored to db
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Int = 0
}

@Dao
interface NotifyDao {
    @Query(
        """
            SELECT t1.rowid,
                t1.notify_id,
                t1.title,
                t1.content,
                t1.package_name,
                t1.display_name,
                t1.created_time,
                t1.small_icon,
                t1.large_icon,
                t1.channel,
                t1.deleted_time,
                t1.priority,
                t2.`count`
            FROM tb_notify t1
            INNER JOIN (SELECT MAX(rowid) AS id, COUNT(*) AS `count` FROM tb_notify WHERE deleted_time IS NULL GROUP BY package_name, notify_id) t2
                ON t1.rowid = t2.id
            WHERE deleted_time IS NULL
            ORDER BY created_time DESC
        """
    )
    fun getNotifies(): PagingSource<Int, Notify>

    @Query(
        """
            SELECT rowid,
                notify_id,
                title,
                content,
                package_name,
                display_name,
                created_time,
                small_icon,
                large_icon,
                channel,
                deleted_time,
                priority,
                `count`
            FROM tb_notify
            WHERE notify_id = :notifyId
                AND package_name = :packageName
                AND deleted_time IS NULL
            ORDER BY created_time DESC
        """
    )
    fun getByNotifyId(notifyId: Int, packageName: String): PagingSource<Int, Notify>

    @Insert
    suspend fun insertNotify(item: Notify)

    @Query("DELETE FROM tb_notify WHERE notify_id = :notifyId AND package_name = :packageName AND deleted_time IS NOT NULL")
    suspend fun deleteByGroup(notifyId: Int, packageName: String)

    @Query("UPDATE tb_notify SET deleted_time = DATETIME('now') WHERE notify_id = :notifyId AND package_name = :packageName AND deleted_time IS NULL")
    suspend fun markDeletedByGroup(notifyId: Int, packageName: String)

    @Query("UPDATE tb_notify SET deleted_time = NULL WHERE notify_id = :notifyId AND package_name = :packageName AND deleted_time IS NOT NULL")
    suspend fun undoDeleteByGroup(notifyId: Int, packageName: String)

    @Query("DELETE FROM tb_notify WHERE rowid = :id AND deleted_time IS NOT NULL")
    suspend fun deleteById(id: Int)

    @Query("UPDATE tb_notify SET deleted_time = NULL WHERE rowid = :id AND deleted_time IS NOT NULL")
    suspend fun undoDeleteById(id: Int)

    @Query("UPDATE tb_notify SET deleted_time = DATETIME('now') WHERE rowid = :id AND deleted_time IS NULL")
    suspend fun markDeletedById(id: Int)

    @Query(
        """
            SELECT rowid,
                notify_id,
                title,
                content,
                package_name,
                display_name,
                created_time,
                small_icon,
                large_icon,
                channel,
                deleted_time,
                priority,
                `count`
            FROM tb_notify
            WHERE tb_notify MATCH :query
                AND deleted_time IS NULL
        """
    )
    fun searchByQuery(query: String): PagingSource<Int, Notify>

    @Query(
        """
            SELECT COUNT(*) AS `value`,
                display_name AS `label`,
                package_name,
                1.0 * COUNT(*) / (SELECT COUNT(*) FROM tb_notify WHERE deleted_time IS NULL AND created_time >= :dataTime) AS `dest`
            FROM tb_notify
            WHERE deleted_time IS NULL
                AND created_time >= :dataTime
            GROUP BY package_name
            ORDER BY value DESC
        """
    )
    fun statisticByPackageName(dataTime: LocalDateTime): Flow<List<PieChartData>>
}

data class AppIcon(
    val icon: Drawable? = null,
    val largeIcon: Drawable? = null,
    val appDisplayName: String = "Unknown"
)

data class PieChartData(
    val value: Long,
    val label: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    val dest: Float
) {
    fun toVO(context: Context): PieChartDataVO {
        val icon = IconUtils.getPackageIcon(packageName, context)
        return PieChartDataVO(
            value = value,
            label = label,
            dest = dest,
            appIcon = AppIcon(
                icon = icon,
                largeIcon = icon,
                appDisplayName = label
            )
        )
    }
}