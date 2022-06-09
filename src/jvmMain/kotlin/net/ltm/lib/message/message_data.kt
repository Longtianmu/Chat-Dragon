package net.ltm.lib.message

import kotlinx.serialization.Serializable

data class Messages(
    val msgID: String,
    val relationID: String,
    val contactID: String,
    val timeStamp: Long,
    val messageContent: String
)

@Serializable
data class StorageMessages(
    val sender: String,
    val senderName: String,
    val senderAvatar: String,
    val content: MutableList<Content>
)

@Serializable
data class Content(
    val type: String,
    val content: String
)

