package com.example.easyfit.ui.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.easyfit.*
import com.example.easyfit.databinding.FragmentHomeBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

data class CalConsumed (var cal: Int) {
    var value: Int = cal
    override fun toString(): String { return cal.toString() }
    fun set(num: Int) { cal = num }
    fun get(): Int { return cal }
    fun addTo (num: Int) {cal += num}
}

data class CalBurn (var cal: Int) {
    override fun toString(): String { return cal.toString() }
    fun set(num: Int) { cal = num }
    fun get(): Int { return cal }
    fun addTo (num: Int) {cal += num}
}

val caloriesConsumed = CalConsumed(0)
val caloriesBurned = CalBurn(0)

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val _textRDI = view.findViewById<TextView>(R.id.textRDI)
        val _textConsumed = view.findViewById<TextView>(R.id.textCaloriesConsumed)
        val _textBurned = view.findViewById<TextView>(R.id.textCaloriesBurned)

        //set RDI, cal consumed, cal burned (top of screen)
        _textRDI.text = RDIuser
        _textConsumed.text = caloriesConsumed.toString()
        _textBurned.text = caloriesBurned.toString()

        //mutable list for list view
        val dataFood = mutableListOf<String>()
        val dataExercise = mutableListOf<String>()

        //list view Food
        val lvadapterFood: ArrayAdapter<String> = ArrayAdapter(activity?.applicationContext!!, R.layout.itemhistory, dataFood)
        val lvTodayFood = view.findViewById<ListView>(R.id.lvTodayFood)
        lvTodayFood.adapter = lvadapterFood
        //list view Exercise
        val lvadapterExercise: ArrayAdapter<String> = ArrayAdapter(activity?.applicationContext!!, R.layout.itemhistory, dataExercise)
        val lvTodayExercise = view.findViewById<ListView>(R.id.lvTodayExercise)
        lvTodayExercise.adapter = lvadapterExercise

        //Add Food Button
        val _btnAddFood = view.findViewById<Button>(R.id.btnAddFood)
        _btnAddFood.setOnClickListener {
            if(RDIuser == "0") { //if RDI is not calculated yet, force move to Calculator tab
                Toast.makeText(activity?.applicationContext, "Please calculate RDI", Toast.LENGTH_LONG).show()
                //move to Calc page
                setSelectedPage(R.id.navigation_profile)
            }
            else {
                val intentAddFood = Intent(activity?.applicationContext, addFoodActivity::class.java)
                startActivity(intentAddFood)
            }
        }

        //Add Exercise Button
        val _btnExercise = view.findViewById<Button>(R.id.btnExercise)
        _btnExercise.setOnClickListener {
            if(RDIuser == "0") { //if RDI is not calculated yet, force move to Calculator tab
                Toast.makeText(activity?.applicationContext, "Please calculate RDI", Toast.LENGTH_LONG).show()
                //move to Calc page
                setSelectedPage(R.id.navigation_profile)
            }
            else {
                val intentExercise = Intent(activity?.applicationContext, addExerciseActivity::class.java)
                startActivity(intentExercise)
            }
        }

        //function to get food eaten today, show on list view Food
        getFood(dataFood, lvadapterFood, _textConsumed, lvTodayFood)
        //function to get exercise done today, show on list view Food
        getExercise(dataExercise, lvadapterExercise, _textBurned, lvTodayExercise)

        //calculate calorie deficit
        val calorieDeficit = (RDIuser.toInt() - caloriesConsumed.get() + caloriesBurned.get()).toString()

        //add calorie deficit history if there is activity
        if(caloriesConsumed.get() > 0  || caloriesBurned.get() > 0)
            addDeficitHistory(calorieDeficit)
    }

    private fun addDeficitHistory(calorieDeficit: String) {
        val today = Calendar.getInstance()
        var day:String = today.get(Calendar.DATE).toString()
        var month:String = (today.get(Calendar.MONTH) + 1).toString()
        val year:String = today.get(Calendar.YEAR).toString()
        if(month.toInt() < 10) month = "0$month"
        if(day.toInt() < 10) day = "0$day"
        val date = "$year-$month-$day"

        val key = "$loggedInUser | $date"

        val data = deficithistoryFormat(loggedInUser, calorieDeficit, date)
        Log.d(TAG, "homefrag\n$loggedInUser\n$calorieDeficit\n$date\n$key\n$data")
        db.collection("dataDeficitHistory").document(key).set(data)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Data calorie deficit successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addDataFood(document: QueryDocumentSnapshot, dataFood: MutableList<String>) {
        var str = ""

        //concat food name, calorie
        str += "${document.data["foodname"].toString()}\n"+
                "${document.data["calorie"].toString()} kcal"

        //add str to mutable list for listview
        dataFood.add(str)
    }

    private fun addDataExercise(document: QueryDocumentSnapshot, dataExercise: MutableList<String>) {
        var str = ""

        //concat exercise name, duration, calorie
        str += "${document.data["exercisename"].toString()}\n" +
                "${document.data["duration"].toString()} min | " +
                "${document.data["calorie"].toString()} kcal"

        //add str to mutable list for listvieww
        dataExercise.add(str)
    }

    private fun getFood(
        dataFood: MutableList<String>,
        lvadapterFood: ArrayAdapter<String>,
        _textConsumed: TextView,
        lvTodayFood: ListView
    ) {
        //get today's date
        val today = Calendar.getInstance()
        var day:String = today.get(Calendar.DATE).toString()
        var month:String = (today.get(Calendar.MONTH) + 1).toString()
        val year:String = today.get(Calendar.YEAR).toString()
        if(month.toInt() < 10) month = "0$month"
        if(day.toInt() < 10) day = "0$day"
        val date = "$year-$month-$day"

        db.collection("dataFoodHistory")
            .orderBy("date")
            .get()
            .addOnSuccessListener { result ->
                //clear mutable list before getting data
                dataFood.clear()
                for (document in result) {
                    //check if data's date == today's date & data's user == current logged in user
                    if (document.data["date"].toString() == date && document.data["username"].toString() == loggedInUser) {
                        addDataFood(document, dataFood)
                    }
                }
                //count total calories consumed today
                caloriesConsumed.set(0) //reset cal consumed stack to 0
                for (i in dataFood.indices) { //calories consumed += each food eaten today
                    caloriesConsumed.addTo(dataFood[i].split("\n")[1].split(" ")[0].toInt())
                    //additional number for each food
                    dataFood[i] = "${i + 1}) ${dataFood[i]}"
                }

                if(caloriesConsumed.get() > 0) {
                    //append total calories consumed to top of list view
                    dataFood.add(0, "Total calories = $caloriesConsumed kcal")
                    //update cal consumed on top of screen
                    _textConsumed.text = caloriesConsumed.toString()
                    //show list view (default is gone)
                    lvTodayFood.visibility = View.VISIBLE
                }
                else { //if no cal consumed, keep listview hidden
                    lvTodayFood.visibility = View.GONE
                }
                lvadapterFood.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun getExercise(
        dataExercise: MutableList<String>,
        lvadapterExercise: ArrayAdapter<String>,
        _textBurned: TextView,
        lvTodayExercise: ListView
    ) {
        //get today's date
        val today = Calendar.getInstance()
        var day:String = today.get(Calendar.DATE).toString()
        var month:String = (today.get(Calendar.MONTH) + 1).toString()
        val year:String = today.get(Calendar.YEAR).toString()
        if(month.toInt() < 10) month = "0$month"
        if(day.toInt() < 10) day = "0$day"
        val date = "$year-$month-$day"

        db.collection("dataExerciseHistory")
            .orderBy("date")
            .get()
            .addOnSuccessListener { result ->
                //clear mutable list before getting data
                dataExercise.clear()
                for (document in result) {
                    //check if data's date == today's date & data's user == current logged in user
                    if (document.data["date"].toString() == date && document.data["username"].toString() == loggedInUser) {
                        addDataExercise(document, dataExercise)
                    }
                }
                //count total calories burned today
                caloriesBurned.set(0) //set cal burned stack to 0
                for (i in dataExercise.indices) { //cal burned += each exercise done today
                    caloriesBurned.addTo(dataExercise[i].split("\n")[1].split(" ")[3].toInt())
                    //additional numbering to each exercise in list view
                    dataExercise[i] = "${i + 1}) ${dataExercise[i]}"
                }

                if(caloriesBurned.get() > 0) {
                    //append total cal burned to top of list view
                    dataExercise.add(0, "Total calories = $caloriesBurned kcal")
                    //update cal burned on top of screen
                    _textBurned.text = caloriesBurned.toString()
                    //show list view (default is gone)
                    lvTodayExercise.visibility = View.VISIBLE
                }
                else { //if no cal burned, keep listview hidden
                    lvTodayExercise.visibility = View.GONE
                }
                lvadapterExercise.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}


