package com.applicforge.umbalarm.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * ☂️ 위치 정보 관리 매니저
 */
@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * 현재 위치 가져오기
     */
    suspend fun getCurrentLocation(): Location? {
        return if (hasLocationPermission()) {
            try {
                withTimeoutOrNull(10000L) { // 10초 타임아웃
                    getCurrentLocationInternal()
                }
            } catch (e: Exception) {
                android.util.Log.e("LocationManager", "위치 정보 가져오기 실패", e)
                null
            }
        } else {
            android.util.Log.w("LocationManager", "위치 권한이 없습니다")
            null
        }
    }

    /**
     * 위치 권한 확인
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 마지막으로 알려진 위치 가져오기
     */
    suspend fun getLastKnownLocation(): Location? {
        return if (hasLocationPermission()) {
            try {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            continuation.resume(location)
                        }
                        .addOnFailureListener { exception ->
                            android.util.Log.e("LocationManager", "마지막 위치 가져오기 실패", exception)
                            continuation.resume(null)
                        }
                }
            } catch (e: Exception) {
                android.util.Log.e("LocationManager", "마지막 위치 가져오기 예외", e)
                null
            }
        } else {
            null
        }
    }

    private suspend fun getCurrentLocationInternal(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result)
                } else {
                    // 현재 위치를 가져올 수 없으면 마지막 위치 시도
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLocation ->
                            continuation.resume(lastLocation)
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                }
            }
        }
    }
} 