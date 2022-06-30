package com.example.easyfit.ui.history

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.easyfit.R
import com.example.easyfit.databinding.FragmentHistoryBinding
import com.example.easyfit.db
import com.example.easyfit.loggedInUser
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    lateinit var sp : SharedPreferences

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var data = mutableListOf<String>()

        //list view adapter
        val lvadapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.itemhistory, data)
        val lvHistory = view.findViewById<ListView>(R.id.lvHistory)
        lvHistory.adapter = lvadapter

        var btnDaily = view.findViewById<Button>(R.id.btnDaily)
        var btnMonthly = view.findViewById<Button>(R.id.btnMonthly)
        var btnYearly = view.findViewById<Button>(R.id.btnYearly)

        btnDaily.setOnClickListener {
            getData(0, data, lvadapter, lvHistory) //daily
        }
        btnMonthly.setOnClickListener {
            getData(1, data, lvadapter, lvHistory) //monthly
        }
        btnYearly.setOnClickListener {
            getData(2, data, lvadapter, lvHistory) //Yearly
        }
        //set default page
        btnDaily.performClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getData(
        selectedFilter: Int,
        data: MutableList<String>,
        lvadapter: ArrayAdapter<String>,
        lvHistory: ListView
    ) {
        //check filter d,m,y && current logged in user

        var lastAddedMonth = ""
        var lastAddedYear = ""

        var deficitStack = 0
        var counter = 0

        var day = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        var month = arrayListOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        db.collection("dataDeficitHistory")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                data.clear()
                for(document in result) {
                    if(loggedInUser == document.data["username"].toString()) {
                        val curDay = document.data["date"].toString().split("-")[2]
                        val curMonth = document.data["date"].toString().split("-")[1]
                        val curYear = document.data["date"].toString().split("-")[0]
                        val date = "$curYear-$curMonth-$curDay"
                        val curDate = SimpleDateFormat("yyyy-MM-dd").parse(date)

                        val curDeficit = document.data["deficit"].toString().toInt()

                        val tgl = Calendar.getInstance()
                        tgl.time = curDate
                        val dayOfWeek = tgl.get(Calendar.DAY_OF_WEEK)

                        if(selectedFilter == 0) { //day
                            //init lastAddedMonth & Year
                            if(lastAddedYear == "") {
                                lastAddedYear = curYear
                                data.add("=================== $curYear ===================")
                            }
                            if(lastAddedMonth == "") {
                                lastAddedMonth = curMonth
                                data.add("\t---------- ${month[curMonth.toInt() - 1]} ----------")
                            }

                            if(lastAddedYear != curYear) {
                                lastAddedYear = curYear
                                lastAddedMonth = curMonth
                                data.add("=================== $curYear ===================")
                                data.add("\t---------- ${month[curMonth.toInt() - 1]} ----------")
                            }
                            if(lastAddedMonth != curMonth) {
                                lastAddedMonth = curMonth
                                data.add("\t---------- ${month[curMonth.toInt() - 1]} ----------")
                            }

                            data.add("\t\t${day[dayOfWeek - 1]} $curDay | $curDeficit kcal")
                        }
                        else if (selectedFilter == 1) { //month
                            //init lastAddedMonth & Year
                            if(lastAddedYear == "") {
                                lastAddedYear = curYear
                                data.add("=================== $curYear =================== ")
                            }
                            if(lastAddedMonth == "") {
                                lastAddedMonth = curMonth
                            }

                            if(lastAddedYear == curYear && lastAddedMonth == curMonth) //if year is same keep adding
                                deficitStack += curDeficit
                            if (lastAddedYear != curYear) { //diff year
                                data.add("\t${month[lastAddedMonth.toInt() - 1]} | $deficitStack kcal")

                                deficitStack = curDeficit
                                lastAddedYear = curYear
                                lastAddedMonth = curMonth
                                data.add("=================== $curYear =================== ")
                            }
                            else if (lastAddedMonth != curMonth) { //diff month
                                data.add("\t${month[lastAddedMonth.toInt() - 1]} | $deficitStack kcal")

                                deficitStack = curDeficit
                                lastAddedMonth = curMonth
                            }


                            if(counter == result.size() - 1) {
                                if (lastAddedYear != curYear) { //diff year
                                    data.add("${month[lastAddedMonth.toInt() - 1]} | $deficitStack kcal")
                                    deficitStack = curDeficit
                                    lastAddedYear = curYear
                                    lastAddedMonth = curMonth
                                    data.add("=================== $curYear =================== ")
                                }
                                else if (lastAddedMonth != curMonth) { //diff month
                                    data.add("\t${month[lastAddedMonth.toInt() - 1]} | $deficitStack kcal")
                                    deficitStack = curDeficit
                                    lastAddedMonth = curMonth
                                }

                                data.add("\t${month[lastAddedMonth.toInt() - 1]} | $deficitStack kcal")
                            }
                        }
                        else if (selectedFilter == 2) { //year
                            if(lastAddedYear == "")
                                lastAddedYear = curYear

                            if(lastAddedYear == curYear) //if year is same keep adding
                                deficitStack += curDeficit
                            else { //if year is different pushback to list
                                data.add("$lastAddedYear | $deficitStack kcal")
                                deficitStack = curDeficit
                                lastAddedYear = curYear
                            }

                            if(counter == result.size() - 1) {
                                data.add("$lastAddedYear | $deficitStack kcal")
                                deficitStack = curDeficit
                            }
                        }
                        else {
                            data.add("Wrong filter")
                        }
                    }
                    counter += 1
                }
                if(data.size == 0)
                    data.add("\tYou have no calorie deficit history")
                lvadapter.notifyDataSetChanged()
                //scroll to bottom of list view after updating
                lvHistory.setSelection(lvadapter.count - 1)
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}
