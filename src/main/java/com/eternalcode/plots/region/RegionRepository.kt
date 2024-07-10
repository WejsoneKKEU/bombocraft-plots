package com.eternalcode.plots.region

import java.util.*

interface RegionRepository {
    fun saveRegion(region: Region?)

    fun deleteRegion(region: Region?)

    fun getRegion(regionUUID: UUID?): Region?
}
