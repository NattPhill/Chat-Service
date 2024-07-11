package ru.netology

data class Chat(val chatId: Int, val user1: User, val user2: User, var messages: MutableList<Message>)

data class Message(val messageId: Int, val sender: User, var text: String, var isRead: Boolean = false)

class User(val userId: Int, val userName: String)
class NoteNotFoundException(message: String) : RuntimeException(message)

object ChatService {
    var chats = mutableListOf<Chat>()
    var messages = mutableListOf<Message>()
    var users = mutableMapOf<Int, User>()

    fun addUser(user: User) {
        users[user.userId] = user
    }

    // Создать новое сообщение.
    // Создать чат. Чат создаётся, когда пользователю отправляется первое сообщение.
    fun createMessage(userId: Int, message: Message): Message {
        val user = users[userId] ?: throw NoteNotFoundException("Пользователь не найден")

        // Проверяем, существует ли чат между отправителем и получателем
        val existingChat =
            chats.asSequence()
                .find { it.user1 == user && it.user2 == message.sender || it.user1 == message.sender && it.user2 == user }

        if (existingChat == null) {
            // Если чата нет, создаем новый чат
            val chatId = chats.size + 1
            val newChat = Chat(chatId, user, message.sender, mutableListOf(message))
            chats += newChat
        } else {
            // Если чат существует, добавляем сообщение в существующий чат
            existingChat.messages.add(message)
        }

        // Добавляем сообщение в список сообщений
        messages += message.copy(messageId = messages.size + 1)
        return messages.last()
    }

    //Видеть, сколько чатов не прочитано (например, service.getUnreadChatsCount).
    // В каждом из таких чатов есть хотя бы одно непрочитанное сообщение.
    fun getUnreadChatsCount(): Int = chats.asSequence()
        .filter { chat -> chat.messages.any { !it.isRead } }
        .count()

    //Получить список чатов (например, service.getChats).
    //fun getChats(): MutableList<Chat> = chats

    //Получить список последних сообщений из чатов (можно в виде списка строк).
    // Если сообщений в чате нет (все были удалены), то пишется «нет сообщений».
    fun getLastMessages(): List<String> {
        return chats.asSequence()
            .map { chat ->
                val lastMessage = chat.messages.lastOrNull()
                if (lastMessage != null) {
                    "${lastMessage.text}"
                } else {
                    "Нет сообщений"
                }
            }
            .toList()
    }

    //    Получить список сообщений из чата, указав:
    //    ID собеседника;
    //    количество сообщений.
    //    После того как вызвана эта функция, все отданные сообщения автоматически считаются прочитанными.
    fun getMessagesFromChat(userId: Int, count: Int): List<Message> {
        val chat = chats.asSequence()
            .find { it.user1.userId == userId || it.user2.userId == userId }
            ?: throw IllegalArgumentException("Чат с пользователем с ID $userId не найден")

        return chat.messages.asSequence()
            .toList()       // Преобразуем в список, чтобы использовать takeLast
            .takeLast(count)
            .asSequence()      // Преобразуем обратно в последовательность для ленивой обработки
            .onEach { it.isRead = true }
            .toList()
    }

    //редактировать сообщение
    //fun editMessage
    fun editMessage(messageId: Int, newText: String) {
        val message = messages.asSequence().find { it.messageId == messageId }
            ?: throw IllegalArgumentException("Сообщение с ID $messageId не найдено")

        message.text = newText
    }

    //Удалить сообщение
    fun deleteMessage(messageId: Int) {
        val messageIndex = messages.indexOfFirst { it.messageId == messageId }
        if (messageIndex != -1) {
            messages.removeAt(messageIndex)
        } else {
            throw IllegalArgumentException("Сообщение с ID $messageId не найдено")
        }
    }

    //Удалить чат, т. е. целиком удалить всю переписку.
    fun deleteChat(chatId: Int) {
        val chatIndex = chats.indexOfFirst { it.chatId == chatId }
        if (chatIndex != -1) {
            val chatToDelete = chats.removeAt(chatIndex)
            messages.removeAll { it in chatToDelete.messages }
        } else {
            throw IllegalArgumentException("Чат с ID $chatId не найден")
        }
    }
}

fun main() {
    val chatService = ChatService

    // Добавить пользователей
    val user1 = User(1, "Mike")
    val user2 = User(2, "Bobby")
    chatService.addUser(user1)
    chatService.addUser(user2)

    // Создать чат

    // Создать сообщения
    chatService.createMessage(user2.userId, Message(1, user1, "Привет, Майк!"))
    chatService.createMessage(user1.userId, Message(2, user2, "Привет, Бобби =)"))
    chatService.createMessage(user1.userId, Message(3, user2, "Как дела?"))

    // Количество непрочитанных чатов
    val unreadChatsCount = chatService.getUnreadChatsCount()
    println("Количество непрочитанных чатов: $unreadChatsCount")

    // список чатов
    println("Список чатов:")
    println(ChatService.chats)

    // Прочитать последние сообщения
    val lastMessages = chatService.getLastMessages()
    println("Последние сообщения из чатов:")
    lastMessages.forEach { println(it) }

    // Получаем сообщения из чата
    val messagesFromChat = chatService.getMessagesFromChat(user1.userId, 2)
    println("Сообщения из чата:")
    messagesFromChat.forEach { println(it) }

    // Редактируем сообщение
    chatService.editMessage(3, "Как твои дела?")

    // Удаляем сообщение
    chatService.deleteMessage(2)

    // Удаляем чат
    chatService.deleteChat(1)
}