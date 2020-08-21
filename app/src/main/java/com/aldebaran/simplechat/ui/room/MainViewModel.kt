package com.aldebaran.simplechat.ui.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aldebaran.simplechat.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainViewModel: ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firebaseRef by lazy { FirebaseDatabase.getInstance().reference }
    private lateinit var firebaseListener: ValueEventListener

    private val TAG = "MainViewModel"

    private val _chat by lazy { MutableLiveData<List<Chat>>() }
    val chat: LiveData<List<Chat>>
        get() = _chat

    private val _bubbleChat by lazy { MutableLiveData<Chat>() }
    val bubbleChat: LiveData<Chat>
        get() = _bubbleChat

    private val MESSAGE = "message"

    fun actionLogout(){
        auth.signOut()
    }

    fun getUserName(): String{
        val email = auth.currentUser?.email.orEmpty()
        return email.split("@").first()
    }

    fun streamDb(){
        firebaseListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataChat = snapshot.children.toMutableList().map { child ->
                    val message = child.getValue(Chat::class.java)
                    message?.key = child.key
                    message ?: Chat()
                }
                _chat.value = dataChat
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, databaseError.message)
            }

        }

        firebaseRef.child(MESSAGE)
            .addValueEventListener(firebaseListener)
    }

    fun sendMessage(message: String, from: String){
        val messageObj =
            Chat(from, message)
        firebaseRef.child(MESSAGE).push().setValue(messageObj)
    }

    fun updateChat(chat: Chat){
        val childUpdate = hashMapOf<String, Any>(
            "/$MESSAGE/${chat.key}" to chat.toMap()
        )
        firebaseRef.updateChildren(childUpdate)
    }

    fun deleteChat(chat: Chat){
        firebaseRef.child(MESSAGE)
            .child(chat.key.orEmpty())
            .removeValue()
    }

    fun onBubbleChatLongClick(chat: Chat){
        _bubbleChat.value = chat
    }

    override fun onCleared() {
        firebaseRef.removeEventListener(firebaseListener)
        super.onCleared()
    }
}