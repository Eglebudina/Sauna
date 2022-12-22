package org.wit.sauna.models

class setdata {
    var name: String? = null
    var randomkey: String? = null
    var description: String? = null
    var title: String? = null
    var date: String? = null
    var count: String? = null
    var lat: String? = null
    var lng: String? = null

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