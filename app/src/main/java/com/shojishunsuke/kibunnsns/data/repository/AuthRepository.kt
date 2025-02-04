package com.shojishunsuke.kibunnsns.data.repository

import android.net.Uri

interface AuthRepository {
    fun updateUserName(userName: String)
    fun updateUser()
    suspend fun updateUserPhoto(uri: Uri)
    fun getUserName(): String
    fun getUserPhotoUri(): Uri
    fun getUserId(): String
}