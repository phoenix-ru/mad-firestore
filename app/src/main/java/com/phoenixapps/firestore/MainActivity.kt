package com.phoenixapps.firestore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val STUDENT_ROLL_KEY = "roll_number"
    val STUDENT_NAME_KEY = "student_name"

    var mCollection = FirebaseFirestore.getInstance().collection("students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getStudentData(view: View) {
        val studentRollNumber = studentRollET.text.toString()
        if (studentRollNumber.isEmpty()) {
            Toast.makeText(
                this,
                "Please, provide student roll number",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        mCollection.document(studentRollNumber)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                run {
                    studentNameET.setText(documentSnapshot.get(STUDENT_NAME_KEY).toString())
                }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(this, "No student found", Toast.LENGTH_LONG).show()
            }
    }

    fun addStudent(view: View) {

    }

    fun deleteStudent(view: View) {

    }

    fun dropCollection(view: View) {

    }
}
