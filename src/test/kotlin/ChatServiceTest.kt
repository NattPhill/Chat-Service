import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import ru.netology.Chat
import ru.netology.ChatService
import ru.netology.Message
import ru.netology.User

class ChatServiceTest {

    lateinit var user1: User
    lateinit var user2: User

    @Before
    fun setUp() {
        ChatService.chats.clear()
        ChatService.messages.clear()
        ChatService.users.clear()

        user1 = User(1, "Mike")
        user2 = User(2, "Bobby")

        ChatService.addUser(user1)
        ChatService.addUser(user2)
    }

    @Test
    fun addUser() {
        val user3 = User(3, "Marta")
        ChatService.addUser(user3)
        assertEquals(user3, ChatService.users[user3.userId])
    }

    @Test
    fun createMessage() {
        val message = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(2, message)

        assertEquals(1, ChatService.chats.size)
        assertEquals(message, ChatService.chats[0].messages[0])
    }

    @Test
    fun getUnreadChatsCount() {
        val message1 = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(user2.userId, message1)

        assertEquals(1, ChatService.getUnreadChatsCount())
    }

    @Test
    fun getLastMessages() {
        val message1 = Message(0, user1, "Hello, Bobby!")
        ChatService.createMessage(user2.userId, message1)

        val lastMessages = ChatService.getLastMessages()
        assertEquals(1, lastMessages.size)
        assertEquals("Hello, Bobby!", lastMessages[0])
    }

    @Test
    fun getMessagesFromChat() {
        val message1 = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(user2.userId, message1)

        val messagesFromChat = ChatService.getMessagesFromChat(user2.userId, 1)
        assertEquals(1, messagesFromChat.size)
        assertTrue(messagesFromChat[0].isRead)
    }

    @Test
    fun editMessage() {
        val message1 = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(user2.userId, message1)

        ChatService.editMessage(1, "Hello, Bobby! How are you?")
        assertEquals("Hello, Bobby! How are you?", ChatService.messages[0].text)
    }

    @Test
    fun deleteMessage() {
        val message1 = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(user2.userId, message1)

        ChatService.deleteMessage(1)
        assertTrue(ChatService.messages.isEmpty())
    }

    @Test
    fun deleteChat() {
        val message1 = Message(0, user1, "Hello, Bobby")
        ChatService.createMessage(user2.userId, message1)

        ChatService.deleteChat(1)
        assertTrue(ChatService.chats.isEmpty())
    }
}