package org.wit.sauna.models

class credentials {
    var name: String? = null
    var bmi = 0
    var height: String? = null
    var weight: String? = null
    var bloodtype: String? = null
    var pass: String? = null
    var email: String? = null
    var status: String? = null
    var temperature: String? = null
    var sick: String? = null
    var disease: String? = null
    var url: String? = null
    var id: String? = null
    var age: String? = null

    constructor(
        names: String?,
        pass: String?,
        mail: String?,
        status: String?,
        id: String?,
        age: String?,
    ) {
        this.email = mail
        this.pass = pass
        this.id = id
        this.age = age
        this.name = names
        this.status = status
    }

/*    constructor(email: String?, temperature: String?, sick: String?, disease: String?) {
        this.email = email
        this.temperature = temperature
        this.sick = sick
        this.disease = disease
    }*/


    constructor(email: String?, temperature: String?, sick: String?) {
        this.email = email
        this.temperature = temperature
        this.sick = sick
    }

    constructor(email: String?,names: String?, astatus: String?,urls: String?) {
        this.email = email
        this.status =astatus
        this.url = urls
        this.name = names



    }

    constructor(bmi: Int, height: String?, weight: String?, bloodtype: String?) {
        this.bmi = bmi
        this.height = height
        this.weight = weight
        this.bloodtype = bloodtype
    }

    constructor() {}
}