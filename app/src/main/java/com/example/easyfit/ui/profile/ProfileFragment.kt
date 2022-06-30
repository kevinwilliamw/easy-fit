package com.example.easyfit.ui.profile

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
import com.example.easyfit.databinding.FragmentProfileBinding
import com.example.easyfit.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //profile detail box
        val _textName = view.findViewById<TextView>(R.id.textName)
        val _textEmail = view.findViewById<TextView>(R.id.textEmail)

        //tracker box
        val _textCalDef = view.findViewById<TextView>(R.id.textCalDefNumber)
        val _textCalBurn = view.findViewById<TextView>(R.id.textCalBurnNumber)
        val _textCalEat = view.findViewById<TextView>(R.id.textCalEatNumber)

        //profile buttons
        val _btnEditProfile  = view.findViewById<Button>(R.id.btnEditProfile)

        //change profile detail texts
        _textName.text = name
        _textEmail.text = loggedInUser

        //set tracker numbers
        getData(_textCalDef, 0)  //calorie deficit
        getData(_textCalBurn, 1) //calories burned
        getData(_textCalEat, 2)  //calories eaten

        _btnEditProfile.setOnClickListener {
            val intentEditProfile = Intent(activity?.applicationContext, editProfileActivity::class.java)
            startActivity(intentEditProfile)
        }
    }

    private fun optimizeNumber(num: String): String {
        if(num.length == 4) { //for numbers 1000 - 9999
            return "${num[0]},${num.substring(1, num.length)}" //0,000 format
        }
        else if (num.length in 5..6) {
            return "${num.substring(0, (num.length - 3))}.${num[num.length - 3]}K" //000.0K format
        }
        else if(num.length > 6) {
            return "${num.substring(0, (num.length - 6))}.${num[num.length - 6]}M" //000.0M format
        }
        return num
    }

    private fun getData(_text: TextView, i: Int) {

        //set collection name
        var dataCollection = ""
        if(i == 0) dataCollection = "dataDeficitHistory"
        else if (i == 1) dataCollection = "dataExerciseHistory"
        else if (i == 2) dataCollection = "dataFoodHistory"

        //set column name
        var dataColumn = ""
        if(i == 0) dataColumn = "deficit"
        else if (i == 1 || i == 2) dataColumn = "calorie"

        var totalCal = 0

        db.collection(dataCollection)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["username"].toString() == loggedInUser) {
                        totalCal += document.data[dataColumn].toString().toInt()
                    }
                }
                _text.text = optimizeNumber(totalCal.toString())
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
