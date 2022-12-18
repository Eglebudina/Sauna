package org.wit.sauna.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class SaunaMemStore : SaunaStore {

    val saunas = ArrayList<SaunaModel>()

    override fun findAll(): List<SaunaModel> {
        return saunas
    }

    override fun create(sauna: SaunaModel) {
        sauna.id = getId()
        saunas.add(sauna)
        logAll()
    }

    override fun update(sauna: SaunaModel) {
        var foundSauna: SaunaModel? = saunas.find { p -> p.id == sauna.id }
        if (foundSauna != null) {
            foundSauna.title = sauna.title
            foundSauna.description = sauna.description
            foundSauna.image = sauna.image
            foundSauna.lat = sauna.lat
            foundSauna.lng = sauna.lng
            foundSauna.zoom = sauna.zoom
            logAll()
        }
    }


    private fun logAll() {
        saunas.forEach { i("$it") }
    }
}