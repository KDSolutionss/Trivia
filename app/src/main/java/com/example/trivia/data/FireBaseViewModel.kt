package com.example.trivia.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trivia.database.QuestionFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random
import kotlin.random.nextInt


class FireBaseViewModel:ViewModel() {
    var _entity = MutableLiveData<QuestionFirebase>()
    val ref = FirebaseDatabase.getInstance().reference
    fun go()
    {

//        var questRef = ref.child("questions/${Random.nextInt(3004)}")
//        val valueEventListener: ValueEventListener = object : ValueEventListener {
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val a=dataSnapshot.getValue(QuestionFirebase::class.java)
//
//                _entity= MutableLiveData(dataSnapshot.getValue(QuestionFirebase::class.java))
//
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d("error", databaseError.message)
//
//            }
//
//        }
        ref.child("questions/${Random.nextInt(3004)}").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val a = dataSnapshot.getValue(QuestionFirebase::class.java)

                _entity = MutableLiveData(a)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.message)

            }
        })
    }


}

