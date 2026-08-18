/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rve.telemetryf1.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.rve.telemetryf1.data.local.database.Telemetry
import com.rve.telemetryf1.data.local.database.TelemetryDao

/**
 * Unit tests for [DefaultTelemetryRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultTelemetryRepositoryTest {

    @Test
    fun telemetrys_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultTelemetryRepository(FakeTelemetryDao())

        repository.add("Repository")

        assertEquals(repository.telemetrys.first().size, 1)
    }

}

private class FakeTelemetryDao : TelemetryDao {

    private val data = mutableListOf<Telemetry>()

    override fun getTelemetrys(): Flow<List<Telemetry>> = flow {
        emit(data)
    }

    override suspend fun insertTelemetry(item: Telemetry) {
        data.add(0, item)
    }
}
