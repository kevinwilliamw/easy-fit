package com.example.easyfit

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

var dietgoal = ""

class editProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.title = "BMI & RDI Calculator"

        // Editable Variable
        val _EditAge = findViewById<EditText>(R.id.EditAge)
        val _EditWeight = findViewById<EditText>(R.id.EditWeight)
        val _EditHeight = findViewById<EditText>(R.id.EditHeight)
        // Button
        val _CalculateButton = findViewById<Button>(R.id.CalculateButton)
        val _ReCalculateButton = findViewById<Button>(R.id.ReCalculateButton)
        // Change Display
        val _ProfileName = findViewById<TextView>(R.id.ProfileName)
        _ProfileName.text = "Hello, $loggedInUser"
        val _BMIDisplay = findViewById<TextView>(R.id.BMIDisplay)
        val _BMI = findViewById<TextView>(R.id.BMI)
        val _BMRDisplay = findViewById<TextView>(R.id.BMRDisplay)
        val _BMR = findViewById<TextView>(R.id.BMR)
        val _RDIDisplay = findViewById<TextView>(R.id.RDIDisplay)
        val _RDI = findViewById<TextView>(R.id.RDI)
        val _Status = findViewById<TextView>(R.id.Status)

        var RDI = RDIuser

        // Logic

        // Spinner
        val _DietGoalSpinner = findViewById<Spinner>(R.id.DropdownDietGoal)
        val adapterdietgoal = this.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Goals_Array,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        _DietGoalSpinner.adapter = adapterdietgoal
        val _GenderSpinner = findViewById<Spinner>(R.id.DropdownGender)
        val adaptergender = this.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.Gender_Array,
                android.R.layout.simple_spinner_dropdown_item
            )
        }
        _GenderSpinner.adapter = adaptergender
        _CalculateButton.setOnClickListener {
            // Check if the height EditText and Weight EditText are not empty
            if (_EditAge.text.isNotEmpty() &&_EditHeight.text.isNotEmpty() && _EditWeight.text.isNotEmpty()) {
                val gender = _GenderSpinner.selectedItem.toString()
                dietgoal = _DietGoalSpinner.selectedItem.toString()
                val height = (_EditHeight.text.toString()).toFloat()
                val weight = (_EditWeight.text.toString()).toFloat()
                val age = (_EditAge.text.toString()).toInt()

                // calculateBMI will return BMI
                val BMI = CalculateBMI(height, weight)
                _BMI.text = BMI.toString()
                _BMI.visibility = View.VISIBLE

                val BMR = CalculateBMR(age, gender, height, weight)
                _BMR.text = BMR.toString()
                _BMR.visibility = View.VISIBLE

                // update the status text
                if (BMI < 18.5) {
                    _Status.text = "Under Weight"
                } else if (BMI >= 18.5 && BMI < 24.9) {
                    _Status.text = "Healthy Normal Weight"
                } else if (BMI >= 24.9 && BMI < 30) {
                    _Status.text = "Overweight"
                } else if (BMI >= 30) {
                    _Status.text = "Suffering from Obesity"
                }
                //change from string to int
                if (dietgoal == "Weight Loss"){
                    RDI = ((BMR - (0.2 * BMR)).toInt()).toString()
                }
                else if (dietgoal == "Slow Weight Loss"){
                    RDI = ((BMR - (0.1 * BMR)).toInt()).toString()
                }
                else if (dietgoal == "Maintain Body Weight"){
                    RDI = ((BMR + (0.15 * BMR)).toInt()).toString()
                }
                else if (dietgoal == "Slow Weight Gain"){
                    RDI = ((BMR + (0.25 * BMR)).toInt()).toString()
                }
                else if (dietgoal == "Weight Gain"){
                    RDI = ((BMR + (0.35 * BMR)).toInt()).toString()
                }
                _BMR.text = BMR.toString()
                _RDI.text = RDI.toString()
                _BMIDisplay.visibility = View.VISIBLE
                _BMI.visibility
                _Status.visibility = View.VISIBLE
                _BMRDisplay.visibility = View.VISIBLE
                _BMR.visibility = View.VISIBLE
                _RDIDisplay.visibility = View.VISIBLE
                _RDI.visibility = View.VISIBLE
                _ReCalculateButton.visibility = View.VISIBLE
                _CalculateButton.visibility = View.GONE
            }
            // when either Weight EditText or height EditText have null value it will display toast.
            else {
                Toast.makeText(this, "Please fill everything", Toast.LENGTH_SHORT).show()
            }
            addRDI(RDI)
        }
        _ReCalculateButton.setOnClickListener {
            //reset all fields
            /*_CalculateButton.visibility = View.VISIBLE
            _ReCalculateButton.visibility = View.GONE
            _EditAge.text.clear()
            _EditHeight.text.clear()
            _EditWeight.text.clear()
            _Status.text = " "
            _BMIDisplay.visibility = View.GONE
            _BMI.text = " "
            _BMRDisplay.visibility = View.GONE
            _BMR.text = ""
            _RDIDisplay.visibility = View.GONE
            _RDI.text = ""*/
            _CalculateButton.performClick()
        }
    }

    // Function for BMI
    private fun CalculateBMI(height: Float, weight: Float): Float {
        val height_convert_cm_to_m = height / 100
        val BMI = weight / (height_convert_cm_to_m * height_convert_cm_to_m)
        return BMI
    }
    // Function for BMR
    private fun CalculateBMR(age: Int, gender: String, height: Float, weight: Float): Float{
        //BMR laki-laki = 88.362 + (13.397 x BB kg) + (4.799 x TB cm) – (5.677 x umur tahun)
        //BMR Wanita = 447.593 + (9.247 x BB kg) + (3.098 x TB cm) – (4.330 x umur tahun)
        var BMR = 0.0
        if (gender == "Male"){
            BMR = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        }
        else{
            BMR = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
        return BMR.toFloat()
    }

    private fun addRDI(RDI: String) {
        RDIuser = RDI
        db.collection("dataLogin").document(loggedInUser).update(mapOf(
            "rdi" to RDI,
            "dietgoal" to dietgoal
        ))
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Transaction successfully written!")
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }
}