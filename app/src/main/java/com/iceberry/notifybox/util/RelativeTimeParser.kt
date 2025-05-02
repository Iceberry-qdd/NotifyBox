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

import java.time.Duration
import java.time.LocalDateTime

class RelativeTimeParser {
    companion object {
        fun parse(time: LocalDateTime): String {
            val now = LocalDateTime.now()
            return when {
                time.year != now.year -> "${time.year}年${time.monthValue}月${time.dayOfMonth}日"
                time.monthValue != now.monthValue -> "${time.monthValue}月${time.dayOfMonth}日"
                now.dayOfMonth - time.dayOfMonth > 2 -> "${time.monthValue}月${time.dayOfMonth}日"
                now.dayOfMonth - time.dayOfMonth == 2 -> "前天 %02d:%02d".format(time.hour, time.minute)
                now.dayOfMonth - time.dayOfMonth == 1 -> "昨天 %02d:%02d".format(time.hour, time.minute)
                Duration.between(time, now).toMinutes() < 1L -> "${Duration.between(time, now).seconds}秒前"
                else -> "%02d:%02d".format(time.hour, time.minute)
            }
        }
    }
}