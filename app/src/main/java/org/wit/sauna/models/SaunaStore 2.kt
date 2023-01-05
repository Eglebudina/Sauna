package org.wit.sauna.models

interface SaunaStore {
    fun findAll(): List<SaunaModel>
    fun create(sauna: SaunaModel)
    fun update(sauna: SaunaModel)
}