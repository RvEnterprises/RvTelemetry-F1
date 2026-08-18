package com.rve.telemetryf1.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.rve.telemetryf1.data.TelemetryRepository
import com.rve.telemetryf1.data.DefaultTelemetryRepository
import com.rve.telemetryf1.data.PlayerTelemetry
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsTelemetryRepository(
        telemetryRepository: DefaultTelemetryRepository
    ): TelemetryRepository
}

class FakeTelemetryRepository @Inject constructor() : TelemetryRepository {
    override val telemetry: Flow<PlayerTelemetry> = flowOf(PlayerTelemetry(speedKmh = 100))

    override suspend fun startListening() {
        // do nothing
    }
}
