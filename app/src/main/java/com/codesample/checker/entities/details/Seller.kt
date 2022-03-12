package com.codesample.checker.entities.details

data class Seller(
    val connection: Connection,
    val images: Images,
    val isVerified: Boolean,
    val link: String,
    val name: String,
    val online: Boolean,
    val postfix: String,
    val rating: Rating,
    val registrationTime: Int,
    val replyTime: ReplyTime,
    val subscribeInfo: SubscribeInfo,
    val summary: String,
    val title: String,
    val userHash: String,
    val userHashId: String
)