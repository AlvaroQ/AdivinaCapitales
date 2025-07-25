package com.alvaroquintana.domain

data class User(
    var name: String? = "",
    var points: String? = "",
    var score: Int? = 0
) {
    constructor() : this("","",0)
}