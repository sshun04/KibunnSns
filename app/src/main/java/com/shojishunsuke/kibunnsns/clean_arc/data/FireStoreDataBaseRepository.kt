package com.shojishunsuke.kibunnsns.clean_arc.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.shojishunsuke.kibunnsns.clean_arc.data.repository.DataBaseRepository
import com.shojishunsuke.kibunnsns.model.Post
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FireStoreDataBaseRepository : DataBaseRepository {

    private val dataBase = FirebaseFirestore.getInstance()

    override  fun savePost(post: Post) {
        GlobalScope.launch {
            dataBase.collection("posts")
                .document()
                .set(post)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("FireStoreDataBase", "Success")
                    } else {
                        Log.d("FireStoreDatabase", "${it.exception}")
                    }
                }
        }

    }

    override fun getFilteredCollection(fieldName: String, params: Any): List<Post> {

        val results = ArrayList<Post>()

        dataBase.collection("posts")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.forEach {
                        val post = it.toObject(Post::class.java)
                        results.add(post)
                    }
                } else {
                    Log.d("FireStoreDataBase", "Error loading Collection")
                }
            }

        return results
    }

}