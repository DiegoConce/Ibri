package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.messaging.Message
import com.ibri.model.messaging.Room
import com.ibri.repository.ChatRepository
import com.ibri.repository.RoomRepository

class RoomViewModel : ViewModel() {
    val messagesList = MutableLiveData<ArrayList<Message>>()
    val newRoomResponse = MutableLiveData<String>()
    val enterRoomResponse = MutableLiveData<String>()
    val deleteRoomResponse = MutableLiveData<String>()
    val leaveRoomResponse = MutableLiveData<String>()
    val messageResponse = MutableLiveData<String>()
    val selectedRoom = MutableLiveData<Room>()

    val isOwner = MutableLiveData(false)
    val isCreator = MutableLiveData(false)
    val isUserSubscribed = MutableLiveData(false)


    fun createRoom(
        eventId: String,
        name: String,
        description: String,
        maxMembers: Int,
        userId: String,
        media: String
    ) {
        RoomRepository.newRoom(
            newRoomResponse,
            eventId,
            name,
            description,
            maxMembers,
            userId,
            media
        )
    }

    fun enterRoom(userId: String, roomId: String) {
        selectedRoom.value?.let {
            RoomRepository.enterRoom(selectedRoom, userId, roomId)
        }
    }

    fun leaveRoom(userId: String, roomId: String) {
        RoomRepository.leaveRoom(leaveRoomResponse, userId, roomId)
    }

    fun deleteRoom(roomId: String) {
        RoomRepository.deleteRoom(deleteRoomResponse, roomId)
    }

    fun fetchMessages(chatId: String) {
        ChatRepository.getMessages(messagesList, chatId)
    }

    fun sendPrivateMessage(chatId: String, message: String, sender: String) {
        ChatRepository.sendStandEventMessage(messageResponse, chatId, message, sender)
    }

    fun sendCommercialMessage(chatId: String, message: String, sender: String) {
        ChatRepository.sendCommercialMessage(messageResponse, chatId, message, sender)
    }
}