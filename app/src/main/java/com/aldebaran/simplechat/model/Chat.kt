package com.aldebaran.simplechat.model

data class Chat(
    val from: String? = null,
    var message: String? = null,
    var key: String? = null
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "from" to from,
            "message" to message
        )
    }
}