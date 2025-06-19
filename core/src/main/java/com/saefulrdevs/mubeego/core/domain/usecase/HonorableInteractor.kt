package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.HonorableItem
import com.saefulrdevs.mubeego.core.domain.repository.IHonorableRepository

class HonorableInteractor(private val repo: IHonorableRepository) : HonorableUseCase {
    override suspend fun getHonorableMentions(): List<HonorableItem> = repo.getHonorableMentions()
}
