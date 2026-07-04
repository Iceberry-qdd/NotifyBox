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

package com.iceberry.notifybox.util

import android.content.Context
import com.iceberry.notifybox.R
import java.time.Duration
import java.time.LocalDateTime

class RelativeTimeParser {
    companion object {
        fun parse(time: LocalDateTime, context: Context): String {
            val now = LocalDateTime.now()
            val timeStr = "%02d:%02d".format(time.hour, time.minute)
            return when {
                time.year != now.year -> context.getString(R.string.date_full, time.year, time.monthValue, time.dayOfMonth)
                time.monthValue != now.monthValue -> context.getString(
                    R.string.date_month_day,
                    time.monthValue,
                    time.dayOfMonth
                )

                now.dayOfMonth - time.dayOfMonth > 2 -> context.getString(
                    R.string.date_month_day,
                    time.monthValue,
                    time.dayOfMonth
                )

                now.dayOfMonth - time.dayOfMonth == 2 -> context.getString(R.string.date_before_yesterday, timeStr)
                now.dayOfMonth - time.dayOfMonth == 1 -> context.getString(R.string.date_yesterday, timeStr)
                Duration.between(time, now).toMinutes() < 1L -> context.getString(
                    R.string.date_seconds_ago,
                    Duration.between(time, now).seconds
                )

                else -> timeStr
            }
        }
    }
}