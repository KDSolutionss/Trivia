package com.example.trivia.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trivia.database.QuestionFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random


class FireBaseDatabaseViewModel:ViewModel() {
    var entity = MutableLiveData<QuestionFirebase>()
    private val ref = FirebaseDatabase.getInstance().reference
    fun go()
    {
        ref.child("questions/${Random.nextInt(3004)}").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val a = dataSnapshot.getValue(QuestionFirebase::class.java)

                entity = MutableLiveData(a)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.message)

            }
        })
    }


}

