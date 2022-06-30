package com.example.easyfit

import android.content.ContentValues
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

class addFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        supportActionBar?.title = "Add Food"

        val _btnFood = findViewById<Button>(R.id.btnFood)
        val _btnRecent = findViewById<Button>(R.id.btnRecent)
        val _btnAddNewFood = findViewById<Button>(R.id.btnAddNewFood)
        val _btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val _btnShowAll = findViewById<ImageButton>(R.id.btnShowAll)

        val _editSearch = findViewById<EditText>(R.id.editSearch)

        //add food tab
        val _textAddNewFood = findViewById<TextView>(R.id.textAddNewFood)

        val _textFoodName = findViewById<TextView>(R.id.textFoodName)
        val _textFoodPortion = findViewById<TextView>(R.id.textFoodPortion)
        val _textFoodCalorie = findViewById<TextView>(R.id.textFoodCalorie)
        val _textKCAL = findViewById<TextView>(R.id.textKCAL)

        val _editFoodName = findViewById<EditText>(R.id.editFoodName)
        val _editFoodPortion = findViewById<EditText>(R.id.editFoodPortion)
        val _editFoodCalorie = findViewById<EditText>(R.id.editFoodCalorie)

        val _btnAddFood = findViewById<Button>(R.id.btnAddFood)

        //for list view
        val data = mutableListOf<String>()
        //listview
        val lvadapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.itemhistory, data)
        val lvFood = findViewById<ListView>(R.id.lvFood)
        lvFood.adapter = lvadapter

        _btnFood.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Search Food"

            //show food from search bar
            //food tab
            _editSearch.visibility = View.VISIBLE
            _btnSearch.visibility = View.VISIBLE
            _btnShowAll.visibility = View.VISIBLE
            //food & recent tab
            lvFood.visibility = View.VISIBLE
            //add food tab
            _textAddNewFood.visibility = View.GONE
            _textFoodName.visibility = View.GONE
            _textFoodPortion.visibility = View.GONE
            _textFoodCalorie.visibility = View.GONE
            _textKCAL.visibility = View.GONE
            _editFoodName.visibility = View.GONE
            _editFoodPortion.visibility = View.GONE
            _editFoodCalorie.visibility = View.GONE
            _btnAddFood.visibility = View.GONE

            //clear list view
            data.clear()
            lvadapter.notifyDataSetChanged()
        }
        _btnRecent.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Recent Food"

            //show recently eaten
            //food tab
            _editSearch.visibility = View.GONE
            _btnSearch.visibility = View.GONE
            _btnShowAll.visibility = View.GONE
            //food & recent tab
            lvFood.visibility = View.VISIBLE
            //add food tab
            _textAddNewFood.visibility = View.GONE
            _textFoodName.visibility = View.GONE
            _textFoodPortion.visibility = View.GONE
            _textFoodCalorie.visibility = View.GONE
            _textKCAL.visibility = View.GONE
            _editFoodName.visibility = View.GONE
            _editFoodPortion.visibility = View.GONE
            _editFoodCalorie.visibility = View.GONE
            _btnAddFood.visibility = View.GONE

            //clear list view
            data.clear()
            lvadapter.notifyDataSetChanged()

            getRecentFood(data, lvadapter)
        }
        _btnAddNewFood.setOnClickListener {
            //set page title
            //supportActionBar?.title = "Add New Food"

            //show add food page
            _editSearch.visibility = View.GONE
            _btnSearch.visibility = View.GONE
            _btnShowAll.visibility = View.GONE

            lvFood.visibility = View.GONE
            //add food tab
            _textAddNewFood.visibility = View.VISIBLE
            _textFoodName.visibility = View.VISIBLE
            _textFoodPortion.visibility = View.VISIBLE
            _textFoodCalorie.visibility = View.VISIBLE
            _textKCAL.visibility = View.VISIBLE
            _editFoodName.visibility = View.VISIBLE
            _editFoodPortion.visibility = View.VISIBLE
            _editFoodCalorie.visibility = View.VISIBLE
            _btnAddFood.visibility = View.VISIBLE
        }

        _btnSearch.setOnClickListener {
            val searchFood = _editSearch.text.toString()
            getFoodData(data, searchFood, lvadapter, false)
        }

        _editSearch.addTextChangedListener {
            val searchFood = _editSearch.text.toString()
            getFoodData(data, searchFood, lvadapter, false)
        }

        _btnShowAll.setOnClickListener {
            getFoodData(data, "", lvadapter, true)
        }

        _btnAddFood.setOnClickListener {
            if (_editFoodName.text.isEmpty() || _editFoodCalorie.text.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                //get text from editText
                val foodName = _editFoodName.text.toString()
                val calorie = _editFoodCalorie.text.toString()
                val portion = _editFoodPortion.text.toString()

                //add data to firebase
                val data = foodFormat(foodName, portion, calorie)
                db.collection("dataFood").document(foodName).set(data)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "New food successfully written!")
                        Toast.makeText(this, "New food successfully added", Toast.LENGTH_SHORT).show()
                        resetAllFields(_editFoodName, _editFoodPortion, _editFoodCalorie)
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error writing document", e)
                    }
            }
        }

        //set default page to page Food
        _btnFood.performClick()

        lvFood.setOnItemClickListener { adapterView, view, i, l ->
            var selectedFood = lvFood.getItemAtPosition(i).toString().split("\n")[0]
            var selectedFoodCalorie = lvFood.getItemAtPosition(i).toString().split("\n")[1].split(" ")[6]

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

            AlertDialog.Builder(this)
                .setTitle("Add Food")
                .setMessage("Add $selectedFood ?")
                .setPositiveButton(
                    "ADD",
                    DialogInterface.OnClickListener { dialog, which ->
                        addFoodHistory(selectedFood, selectedFoodCalorie, date)
                        Toast.makeText(this, "$selectedFood successfully added", Toast.LENGTH_LONG).show()
                    }
                )
                .setNegativeButton(
                    "CANCEL",
                    DialogInterface.OnClickListener { dialog, which ->
                        //Toast.makeText(this, "$selectedFood is not added", Toast.LENGTH_SHORT).show()
                    }
                )
                .show()
        }
    }

    private fun resetAllFields(
        _editFoodName: EditText,
        _editFoodPortion: EditText,
        _editFoodCalorie: EditText
    ) {
        _editFoodName.text.clear()
        _editFoodPortion.text.clear()
        _editFoodCalorie.text.clear()
    }

    private fun getRecentFood(data: MutableList<String>, lvadapter: ArrayAdapter<String>) {
        db.collection("dataFoodHistory")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                data.clear()
                for (documentFH in result) {
                    if(documentFH.data["username"].toString() == loggedInUser) {
                        addDataRecent(documentFH, data, lvadapter)
                    }
                }
                lvadapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun addDataRecent(
        documentFH: QueryDocumentSnapshot,
        dataFood: MutableList<String>,
        lvadapter: ArrayAdapter<String>
    ) {
        db.collection("dataFood")
            .get()
            .addOnSuccessListener { result ->
                for (documentF in result) {
                    if (documentF.data["name"].toString() == documentFH.data["foodname"].toString()) {
                        addData(documentF, dataFood)
                    }
                }
                if(dataFood.size == 0) {
                    dataFood.add("Food not found. Add new food?")
                }
                lvadapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun getFoodData(
        data: MutableList<String>,
        searchFood: String,
        lvadapter: ArrayAdapter<String>,
        showAll: Boolean,
    ) {
        if(searchFood.isNullOrEmpty() && !showAll) {
            data.clear()
            data.add("Please enter food name")
            lvadapter.notifyDataSetChanged()
        }
        else {
            db.collection("dataFood")
                .orderBy("name")
                .get()
                .addOnSuccessListener { result ->
                    data.clear()
                    for (document in result) {
                        if (document.data["name"].toString().lowercase().contains(searchFood.lowercase())) {
                            addData(document, data)
                        }
                    }
                    if(data.size == 0) {
                        data.add("No food found. Add new food?")
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

        //concat food name
        str += document.data["name"].toString()
        str += "\n"

        //concat portion (gram)

        if(document.data["portion"].toString().isNotEmpty()) {
            str += document.data["portion"].toString()
            str += " gr"
        }
        else {
            str += "1 portion"
        }
        str += " | "

        //concat RDI%
        val calorie = document.data["calorie"].toString()
        val RDIpercentage = ((calorie.toFloat() / RDIuser.toFloat()) * 100).toInt()

        str += "RDI $RDIpercentage% - $calorie kcal"

        data.add(str)
    }

    private fun addFoodHistory(selectedFood: String, selectedFoodCalorie: String, date: String) {
        val data = foodhistoryFormat(loggedInUser, selectedFood, selectedFoodCalorie, date)
        db.collection("dataFoodHistory").document().set(data)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Food history successfully written!")
                //back to home after adding food
                //onBackPressed()
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }
}


