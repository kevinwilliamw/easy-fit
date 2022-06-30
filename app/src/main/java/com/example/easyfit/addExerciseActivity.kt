package com.example.easyfit

import android.content.ContentValues
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.Query

private var min = 0
private var hrtomin = 0
private var totalCaloriesBurned = 0

class addExerciseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        supportActionBar?.title = "Add Exercise"

        val _btnExercise = findViewById<Button>(R.id.btnExercise)
        val _btnRecent = findViewById<Button>(R.id.btnRecent)
        val _btnAddNewExercise = findViewById<Button>(R.id.btnAddNewExercise)
        val _btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val _btnShowAll = findViewById<ImageButton>(R.id.btnShowAll)

        val _editSearch = findViewById<EditText>(R.id.editSearch)

        //add exercise tab
        val _textAddNewExercise = findViewById<TextView>(R.id.textAddNewExercise)

        val _textExerciseName = findViewById<TextView>(R.id.textExerciseName)
        val _textExerciseCalorie = findViewById<TextView>(R.id.textExerciseCalorie)
        val _textExerciseDuration = findViewById<TextView>(R.id.textDuration)
        val _textKCAL = findViewById<TextView>(R.id.textKCAL)
        val _textHR = findViewById<TextView>(R.id.textHR)
        val _textMIN = findViewById<TextView>(R.id.textMIN)

        val _editExerciseName = findViewById<EditText>(R.id.editExerciseName)
        val _editExerciseCalorie = findViewById<EditText>(R.id.editExerciseCalorie)
        val _editExerciseDurationHour = findViewById<EditText>(R.id.editExerciseDurationHour)
        val _editExerciseDurationMin = findViewById<EditText>(R.id.editExerciseDurationMin)

        val _btnAddExercise = findViewById<Button>(R.id.btnAddExercise)

        //for list view
        val data = mutableListOf<String>()
        //listview
        val lvadapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.itemhistory, data)
        val lvExercise = findViewById<ListView>(R.id.lvExercise)
        lvExercise.adapter = lvadapter

        var activePage = 0
        _btnExercise.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Search Exercise"

            //show exercise from search bar
            //exercise tab
            _editSearch.visibility = View.VISIBLE
            _btnSearch.visibility = View.VISIBLE
            _btnShowAll.visibility = View.VISIBLE
            //exercise & recent tab
            lvExercise.visibility = View.VISIBLE
            //add exercise tab
            _textAddNewExercise.visibility = View.GONE
            _textExerciseName.visibility = View.GONE
            _textExerciseCalorie.visibility = View.GONE
            _textExerciseDuration.visibility = View.GONE
            _textKCAL.visibility = View.GONE
            _textHR.visibility = View.GONE
            _textMIN.visibility = View.GONE

            _editExerciseName.visibility = View.GONE
            _editExerciseCalorie.visibility = View.GONE
            _editExerciseDurationHour.visibility = View.GONE
            _editExerciseDurationMin.visibility = View.GONE

            _btnAddExercise.visibility = View.GONE
            //clear list view
            data.clear()
            lvadapter.notifyDataSetChanged()

            activePage = 0
        }
        _btnRecent.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Recent Exercise"

            //show recently eaten
            //exercise tab
            _editSearch.visibility = View.GONE
            _btnSearch.visibility = View.GONE
            _btnShowAll.visibility = View.GONE
            //exercise & recent tab
            lvExercise.visibility = View.VISIBLE
            //add exercise tab
            _textAddNewExercise.visibility = View.GONE
            _textExerciseName.visibility = View.GONE
            _textExerciseCalorie.visibility = View.GONE
            _textExerciseDuration.visibility = View.GONE
            _textKCAL.visibility = View.GONE
            _textHR.visibility = View.GONE
            _textMIN.visibility = View.GONE

            _editExerciseName.visibility = View.GONE
            _editExerciseCalorie.visibility = View.GONE
            _editExerciseDurationHour.visibility = View.GONE
            _editExerciseDurationMin.visibility = View.GONE

            _btnAddExercise.visibility = View.GONE

            //clear list view
            data.clear()
            lvadapter.notifyDataSetChanged()

            getRecentExercise(data, lvadapter)

            activePage = 1
        }
        _btnAddNewExercise.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Add New Exercise"

            //show add exercise page
            _editSearch.visibility = View.GONE
            _btnSearch.visibility = View.GONE
            _btnShowAll.visibility = View.GONE

            lvExercise.visibility = View.GONE
            //add exercise tab
            _textAddNewExercise.visibility = View.VISIBLE
            _textExerciseName.visibility = View.VISIBLE
            _textExerciseCalorie.visibility = View.VISIBLE
            _textExerciseDuration.visibility = View.VISIBLE
            _textKCAL.visibility = View.VISIBLE
            _textHR.visibility = View.VISIBLE
            _textMIN.visibility = View.VISIBLE

            _editExerciseName.visibility = View.VISIBLE
            _editExerciseCalorie.visibility = View.VISIBLE
            _editExerciseDurationHour.visibility = View.VISIBLE
            _editExerciseDurationMin.visibility = View.VISIBLE

            _btnAddExercise.visibility = View.VISIBLE

            activePage = 3
        }

        //set default page to page Exercise
        _btnExercise.performClick()

        //set add new exercise duration max length
        _editExerciseDurationHour.setMaxLength(2)
        _editExerciseDurationMin.setMaxLength(2)

        _btnSearch.setOnClickListener {
            val searchExercise = _editSearch.text.toString()
            getExerciseData(data, searchExercise, lvadapter, false)
        }

        _editSearch.addTextChangedListener {
            val searchExercise = _editSearch.text.toString()
            getExerciseData(data, searchExercise, lvadapter, false)
        }

        _btnShowAll.setOnClickListener {
            getExerciseData(data, "", lvadapter, true)
        }


        _btnAddExercise.setOnClickListener {
            if (_editExerciseName.text.isEmpty() || _editExerciseCalorie.text.isEmpty() || _editExerciseDurationHour.text.isEmpty() || _editExerciseDurationMin.text.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                //get text from editText
                val exercisename = _editExerciseName.text.toString()
                val calorie = _editExerciseCalorie.text.toString()
                var duration = _editExerciseDurationHour.text.toString().toInt() * 60 //add hour
                duration += _editExerciseDurationMin.text.toString().toInt()          //add minute

                //add data to firebase
                val data = exerciseFormat(exercisename, calorie, duration)
                db.collection("dataExercise").document(exercisename).set(data)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Transaction successfully written!")
                        Toast.makeText(this, "Exercise successfully added", Toast.LENGTH_SHORT).show()
                        resetAllFields(_editExerciseName, _editExerciseCalorie, _editExerciseDurationHour, _editExerciseDurationMin)
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error writing document", e)
                    }
            }
        }

        lvExercise.setOnItemClickListener { adapterView, view, i, l ->
            var selectedExercise = lvExercise.getItemAtPosition(i).toString().split("\n")[0]
            var selectedExerciseCalorie = lvExercise.getItemAtPosition(i).toString().split("\n")[1].split(" ")[3]
            var selectedExerciseDuration = lvExercise.getItemAtPosition(i).toString().split("\n")[1].split(" ")[0]
            val caloriesPerMin = selectedExerciseCalorie.toInt()/selectedExerciseDuration.toInt()

/*            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val formatted = current.format(formatter) //current day : String
            val date = LocalDate.parse(formatted, formatter).toString() //turn String to Date*/
            //val weekN = today.get(Calendar.DAY_OF_WEEK) //hari ini minggu ke berapa

            val today = Calendar.getInstance()
            var day:String = today.get(Calendar.DATE).toString()
            var month:String = (today.get(Calendar.MONTH) + 1).toString()
            val year:String = today.get(Calendar.YEAR).toString()
            if(month.toInt() < 10) month = "0$month"
            if(day.toInt() < 10) day = "0$day"
            val date = "$year-$month-$day"

            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_set_duration, null)
            val _editDurationHour  = dialogLayout.findViewById<EditText>(R.id.editDurationHour)
            val _editDurationMin  = dialogLayout.findViewById<EditText>(R.id.editDurationMinute)
            val _textCalBurn  = dialogLayout.findViewById<TextView>(R.id.textCalBurnNumber)

            //set default cal burned per min
            _textCalBurn.text = "${caloriesPerMin} kcal"

            //hour listener
            _editDurationHour.setMaxLength(2)
            _editDurationHour.addTextChangedListener {
                setDialogCalorie(_editDurationMin, _editDurationHour, caloriesPerMin, _textCalBurn)
            }
            //min listener
            _editDurationMin.setMaxLength(2)
            _editDurationMin.addTextChangedListener {
                setDialogCalorie(_editDurationMin, _editDurationHour, caloriesPerMin, _textCalBurn)
            }

            //IF RECENT TAB is active, set history's duration to dialog edit text
            if(activePage == 1) {
                val hours = selectedExerciseDuration.toInt() / 60 //since both are ints, you get an int
                val minutes = selectedExerciseDuration.toInt() % 60

                if(hours > 0) {
                    (_editDurationHour as TextView).text = hours.toString()
                }
                (_editDurationMin as TextView).text = minutes.toString()
            }


            AlertDialog.Builder(this)
                .setTitle("Add Exercise")
                .setMessage("Add $selectedExercise ?")
                .setView(dialogLayout)
                .setPositiveButton(
                    "ADD",
                    DialogInterface.OnClickListener { dialog, which ->
                        var totalDuration = (hrtomin + min).toString()
                        addExerciseHistory(loggedInUser, selectedExercise, totalDuration, totalCaloriesBurned.toString(),  date)
                        Toast.makeText(this, "$selectedExercise successfully added", Toast.LENGTH_LONG).show()
                        //val data = exercisehistoryFormat(loggedInUser, selectedExercise, selectedExerciseCalorie, duration, date)
                    }
                )
                .setNegativeButton(
                    "CANCEL",
                    DialogInterface.OnClickListener { dialog, which ->
                        //Toast.makeText(this, "$selectedExercise is not added", Toast.LENGTH_SHORT).show()
                    }
                )
                .show()
        }
    }

    private fun resetAllFields(
        _editExerciseName: EditText,
        _editExerciseCalorie: EditText,
        _editExerciseDurationHour: EditText,
        _editExerciseDurationMin: EditText
    ) {
        _editExerciseName.text.clear()
        _editExerciseCalorie.text.clear()
        _editExerciseDurationHour.text.clear()
        _editExerciseDurationMin.text.clear()
    }

    private fun getExerciseData(
        data: MutableList<String>,
        searchExercise: String,
        lvadapter: ArrayAdapter<String>,
        showAll: Boolean
    ) {
        if(searchExercise.isNullOrEmpty() && !showAll) {
            data.clear()
            data.add("Please enter exercise name")
            lvadapter.notifyDataSetChanged()
        }
        else {
            db.collection("dataExercise")
                .orderBy("exercisename")
                .get()
                .addOnSuccessListener { result ->
                    data.clear()
                    for (document in result) {
                        if (document.data["exercisename"].toString().lowercase().contains(searchExercise.lowercase())) {
                            addData(document, data)
                        }
                    }
                    if(data.size == 0) {
                        data.add("No exercise found. Add new exercise?")
                    }
                    lvadapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun addData(document: QueryDocumentSnapshot, data: MutableList<String>) {

        var str = ""

        //concat exercise name
        str += "${document.data["exercisename"].toString()}\n"

        //concat duration
        str += "${document.data["duration"].toString()} min | "

        //concat calorie
        str += "${document.data["calorie"].toString()} kcal"

        data.add(str)
    }

    fun setDialogCalorie(
        _editDurationMin: EditText,
        _editDurationHour: EditText,
        caloriesPerMin: Int,
        _textCalBurn: TextView
    ) {
        if(!_editDurationMin.text.isNullOrEmpty()) {
            min = _editDurationMin.text.toString().toInt()
        }
        if (!_editDurationHour.text.isNullOrEmpty()){
            hrtomin = _editDurationHour.text.toString().toInt() * 60
        }
        if(_editDurationMin.text.isNullOrEmpty()) {
            min = 0
        }
        if(_editDurationHour.text.isNullOrEmpty()) {
            hrtomin = 0
        }
        totalCaloriesBurned = (hrtomin + min) * caloriesPerMin
        _textCalBurn.text = "$totalCaloriesBurned kcal"
    }

    private fun addExerciseHistory(
        loggedInUser: String,
        selectedExercise: String,
        totalDuration: String,
        totalCaloriesBurned: String,
        date: String
    ) {
        val data = exercisehistoryFormat(loggedInUser, selectedExercise, totalDuration, totalCaloriesBurned,  date)
        db.collection("dataExerciseHistory").document().set(data)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Transaction successfully written!")
                //back to home after adding exercise
                //onBackPressed()
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    private fun getRecentExercise(data: MutableList<String>, lvadapter: ArrayAdapter<String>) {
        db.collection("dataExerciseHistory")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                data.clear()
                for (document in result) {
                    if(document.data["username"].toString() == loggedInUser) {
                        addDataRecent(document, data)
                    }
                }
                lvadapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun addDataRecent(document: QueryDocumentSnapshot, data: MutableList<String>) {
        var str = ""

        //concat exercise name
        str += "${document.data["exercisename"].toString()}\n"

        //concat duration
        str += "${document.data["duration"].toString()} min | "

        //concat calorie
        str += "${document.data["calorie"].toString()} kcal"

        data.add(str)
    }

    private fun EditText.setMaxLength(i: Int) {
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(i))
    }
}


