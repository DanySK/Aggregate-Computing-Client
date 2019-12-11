package communication

import java.io.Serializable

enum class MessageType { ReadSensor, Execute, Result, Join, ID }

data class Message(val senderUid: Int, val type: MessageType, val content: Any? = null) : Serializable