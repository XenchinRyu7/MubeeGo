package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.HonorableItem

interface IHonorableRepository {
    suspend fun getHonorableMentions(): List<HonorableItem>
}
