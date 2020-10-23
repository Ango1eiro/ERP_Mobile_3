package com.rogoznyak.erp_mobile_3.network

data class NetworkContact(
    val guid: String,
    val name: String,
    val guidCounterpart: String,
    val phone: String)

data class NetworkWorksheet(
    val counterpart: String,
    val duration: String,
    val description: String
)