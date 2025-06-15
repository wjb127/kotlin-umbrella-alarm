package com.applicforge.umbalarm.domain.usecase

import com.applicforge.umbalarm.data.repository.ConfigRepository
import com.applicforge.umbalarm.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(
    private val repository: ConfigRepository
) {
    
    fun execute(appId: String): Flow<Result<AppConfig>> {
        return repository.getAppConfig(appId)
    }
    
    fun getCachedConfig(): AppConfig? {
        return repository.getCachedConfig()
    }
} 