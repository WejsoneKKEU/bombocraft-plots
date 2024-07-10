package com.eternalcode.plots.region

import java.util.*

class RegionService(private val regionSettings: RegionSettings, private val regionRepository: RegionRepository) {
    private val regions: MutableMap<UUID, Region?> = HashMap()

    fun createRegion(x: Int, z: Int): UUID {
        val regionUUID = UUID.randomUUID()
        val region = Region(regionUUID, regionSettings.startSize(), x, z)

        regions[regionUUID] = region
        return regionUUID
    }

    fun getRegion(regionUUID: UUID): Region? {
        return regions.values.stream().filter { region: Region? -> region!!.regionUUID == regionUUID }
            .findFirst().orElse(null)
    }

    fun deleteRegion(regionUUID: UUID) {
        val region = getRegion(regionUUID)
        if (region != null) {
            regions.remove(region)
        }
    }
}
