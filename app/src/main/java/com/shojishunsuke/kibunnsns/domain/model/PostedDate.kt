package com.shojishunsuke.kibunnsns.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_posted_date")
data class PostedDate(@PrimaryKey val dateInLong: Long)