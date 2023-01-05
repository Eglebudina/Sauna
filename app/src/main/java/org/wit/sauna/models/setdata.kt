package org.wit.sauna.models

class setdata {
    var name: String? = "test name"
    var randomkey: String? = "test randomkey"
    var description: String? = "test description"
    var title: String? = "test title"
    var date: String? = "test date"
    var count: String? = "test count"
    var lat: String? = "0.0"
    var lng: String? = "0.0"

    constructor() {}
    constructor(
        name: String?,
        key: String?,
        act: String?,
        count: String?,
        lat: String?,
        lng: String?
    ) {
        this.name = name
        randomkey = key
        this.description = act
        this.count = count
        this.lat = lat
        this.lng = lng
    }

    constructor(amount: String?) {
        title = amount
    }


}