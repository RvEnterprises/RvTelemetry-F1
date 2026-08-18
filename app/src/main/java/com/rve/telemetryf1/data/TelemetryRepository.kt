package com.rve.telemetryf1.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

data class PlayerTelemetry(
    val speedKmh: Int = 0,
    val throttle: Float = 0f,
    val steer: Float = 0f,
    val brake: Float = 0f,
    val gear: Int = 0,
    val engineRPM: Int = 0,
    val engineTemperature: Int = 0,
    val drs: Boolean = false,
    val revLightsPercent: Int = 0
)

interface TelemetryRepository {
    val telemetry: Flow<PlayerTelemetry>
    suspend fun startListening()
}

@Singleton
class DefaultTelemetryRepository @Inject constructor() : TelemetryRepository {

    private val _telemetry = MutableStateFlow(PlayerTelemetry())
    override val telemetry: Flow<PlayerTelemetry> = _telemetry.asStateFlow()

    override suspend fun startListening() {
        withContext(Dispatchers.IO) {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket(20777)
                socket.soTimeout = 0 // block indefinitely
                val buffer = ByteArray(2048)
                val packet = DatagramPacket(buffer, buffer.size)

                while (isActive) {
                    packet.length = buffer.size // Reset buffer size!
                    socket.receive(packet)
                    val byteBuffer = ByteBuffer.wrap(packet.data, 0, packet.length)
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    
                    parsePacket(byteBuffer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                socket?.close()
            }
        }
    }

    private fun parsePacket(buffer: ByteBuffer) {
        // Minimum header size is 29 bytes
        if (buffer.limit() < 29) return

        buffer.position(0)
        
        // Packet Header (29 bytes)
        val packetFormat = buffer.short
        if (packetFormat.toInt() != 2023) return // Only F1 23
        
        val gameYear = buffer.get()
        val gameMajorVersion = buffer.get()
        val gameMinorVersion = buffer.get()
        val packetVersion = buffer.get()
        val packetId = buffer.get().toInt()
        
        val sessionUID = buffer.long
        val sessionTime = buffer.float
        val frameIdentifier = buffer.int
        val overallFrameIdentifier = buffer.int
        val playerCarIndex = buffer.get().toInt() and 0xFF
        val secondaryPlayerCarIndex = buffer.get()

        // Packet ID 6 is Car Telemetry
        if (packetId == 6) {
            // Car Telemetry Packet contains 22 CarTelemetryData objects
            // Each CarTelemetryData is 60 bytes
            val carDataStartOffset = 29
            val playerCarOffset = carDataStartOffset + (playerCarIndex * 60)
            
            if (buffer.limit() < playerCarOffset + 60) return
            
            buffer.position(playerCarOffset)
            val speed = buffer.short.toInt() and 0xFFFF
            val throttle = buffer.float
            val steer = buffer.float
            val brake = buffer.float
            val clutch = buffer.get().toInt() and 0xFF
            val gear = buffer.get().toInt()
            val engineRPM = buffer.short.toInt() and 0xFFFF
            val drs = (buffer.get().toInt() and 0xFF) == 1
            val revLightsPercent = buffer.get().toInt() and 0xFF
            val revLightsBitValue = buffer.short
            
            // skip brakes(8) + tyreSurf(4) + tyreInner(4) = 16 bytes
            buffer.position(buffer.position() + 16)
            
            val engineTemperature = buffer.short.toInt() and 0xFFFF
            
            _telemetry.value = PlayerTelemetry(
                speedKmh = speed,
                throttle = throttle,
                steer = steer,
                brake = brake,
                gear = gear,
                engineRPM = engineRPM,
                engineTemperature = engineTemperature,
                drs = drs,
                revLightsPercent = revLightsPercent
            )
        }
    }
}
