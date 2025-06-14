package com.test.simple.domain.usecase

import com.test.simple.data.repository.ConfigRepository
import com.test.simple.domain.model.AppConfig
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