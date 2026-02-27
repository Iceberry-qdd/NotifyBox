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

package com.iceberry.notifybox.api

import org.junit.Assert
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

class DateTimeTest {

    @Test
    fun test_date_adjust_to_today_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val expect = LocalDateTime.parse("2025-05-31T00:00:00")
        val actual = now.with(LocalTime.MIN)
        println(listOf(actual, now))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_yesterday_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val yesterday = now.minusDays(1L).with(LocalTime.MAX)
        val expect = LocalDateTime.parse("2025-05-30T00:00:00")
        val actual = now.minusDays(1L).with(LocalTime.MAX).with(LocalTime.MIN)
        println(listOf(actual, yesterday))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_this_week_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val expect = LocalDateTime.parse("2025-05-26T00:00:00")
        val actual = now.with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        println(listOf(actual, now.with(LocalTime.MAX).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_this_month_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val expect = LocalDateTime.parse("2025-05-01T00:00:00")
        val actual = now.with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth())
        println(listOf(actual, now.with(LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_last_month_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val expect = LocalDateTime.parse("2025-04-01T00:00:00")
        val actual = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN)
        val endOfMonth = actual.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX)
        println(listOf(actual, endOfMonth))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_this_year_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val expect = LocalDateTime.parse("2025-01-01T00:00:00")
        val actual = now.with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfYear())
        println(listOf(actual, now.with(LocalTime.MAX).with(TemporalAdjusters.lastDayOfYear())))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }

    @Test
    fun test_date_adjust_to_last_year_start() {
        val now = LocalDateTime.parse("2025-05-31T19:14:30")
        val lastYear = now
            .with(LocalTime.MAX)
            .minusYears(1L)
            .with(TemporalAdjusters.lastDayOfYear())
        val expect = LocalDateTime.parse("2024-01-01T00:00:00")
        val actual = lastYear
            .with(LocalTime.MIN)
            .with(TemporalAdjusters.firstDayOfYear())
        println(listOf(actual, lastYear))
        Assert.assertEquals(
            expect.toEpochSecond(ZoneOffset.UTC),
            actual.toEpochSecond(ZoneOffset.UTC)
        )
    }
}