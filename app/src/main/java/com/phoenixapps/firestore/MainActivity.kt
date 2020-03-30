package com.phoenixapps.firestore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val BATCH_LIMIT = 500
    val STUDENT_NAME_KEY = "student_name"

    var mCollection = FirebaseFirestore.getInstance().collection("students")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun getStudentData(view: View) {
        val studentRollNumber = studentRollET.text.toString()
        if (studentRollNumber.isEmpty()) {
            showMessage("Please, provide student roll number")
            return
        }

        mCollection.document(studentRollNumber)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    studentNameET.setText(documentSnapshot.get(STUDENT_NAME_KEY).toString())
                } else {
                    showMessage("Student not found")
                }
            }
            .addOnFailureListener {
                showMessage("Error fetching student")
            }
    }

    fun addStudent(view: View) {
        val studentRollNumber = studentRollET.text.toString()
        val studentName = studentNameET.text.toString()

        if (studentRollNumber.isEmpty()) {
            showMessage("Please, enter roll number")
            return
        } else if (studentName.isEmpty()) {
            showMessage("Please, provide the name")
            return
        }

        mCollection.document(studentRollNumber)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    showMessage("Cannot add an existing student")
                    return@addOnSuccessListener
                } else {
                    mCollection.document(studentRollNumber)
                        .set(hashMapOf("student_name" to studentName))
                        .addOnSuccessListener { run {
                            showMessage("Added")
                            studentRollET.setText("")
                            studentNameET.setText("")
                        }}
                        .addOnFailureListener { showMessage("Failed to add student") }
                }
            }
    }

    fun deleteStudent(view: View) {
        val studentRollNumber = studentRollET.text.toString()

        mCollection.document(studentRollNumber)
            .delete()
            .addOnSuccessListener { showMessage("Deleted") }
            .addOnFailureListener { showMessage("Unable to delete") }
    }

    fun dropCollection(view: View) {
        val db = FirebaseFirestore.getInstance()
        val referenceList = ArrayList<DocumentReference>()

        mCollection.get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    referenceList.add(it.reference)
                }

                if (referenceList.isEmpty()) {
                    showMessage("Empty collection, aborting")
                    return@addOnSuccessListener
                }

                for (i in 0..(referenceList.size / BATCH_LIMIT)) {
                    db.runBatch { batch ->
                        val startIndex = i * BATCH_LIMIT
                        var endIndex = (i + 1) * BATCH_LIMIT
                        if (endIndex > referenceList.size) endIndex = referenceList.size - 1

                        for (ref in referenceList.subList(startIndex, endIndex)) {
                            batch.delete(ref)
                        }
                    }
                }

                showMessage("Successfully dropped the collection")
            }
            .addOnFailureListener { exception ->
                Log.e("firestore", "Batch remove", exception)
                showMessage("Unable to get collection")
            }
    }
}
