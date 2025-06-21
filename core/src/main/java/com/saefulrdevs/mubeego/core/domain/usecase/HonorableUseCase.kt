package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.HonorableItem
import com.saefulrdevs.mubeego.core.domain.repository.IHonorableRepository

interface HonorableUseCase {
    suspend fun getHonorableMentions(): List<HonorableItem>
}
