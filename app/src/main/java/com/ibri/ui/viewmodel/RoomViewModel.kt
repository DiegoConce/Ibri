package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.messaging.Room
import com.ibri.repository.RoomRepository

class RoomViewModel : ViewModel() {
    val newRoomResponse = MutableLiveData<String>()
    val deleteRoomResponse = MutableLiveData<String>()
    val leaveRoomResponse = MutableLiveData<String>()

    val selectedRoom = MutableLiveData<Room>()

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
        RoomRepository.enterRoom(selectedRoom, userId, roomId)
    }

    fun leaveRoom(userId: String, roomId: String) {
        RoomRepository.leaveRoom(leaveRoomResponse, userId, roomId)
    }

    fun deleteRoom(roomId: String) {
        RoomRepository.deleteRoom(deleteRoomResponse, roomId)
    }
}