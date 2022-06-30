package com.example.easyfit.ui.plan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.easyfit.*
import com.example.easyfit.databinding.FragmentPlanBinding
class PlanFragment : Fragment() {

    private lateinit var planViewModel: PlanViewModel
    private var _binding: FragmentPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var RDI = RDIuser
        //Meal Calorie Planning
        var plannedcaloriebreakfast = 0
        var plannedcalorielunch = 0
        var plannedcaloriedinner = 0
        var plannedcaloriesnack = 0
        var plannedcalorielostfromworkout = 0
        //https://www.omnicalculator.com/health/meal-calorie
        if (dietgoal == "Weight Loss"){
            plannedcaloriebreakfast = (RDI.toInt() * 0.3).toInt()
            plannedcalorielunch = (RDI.toInt() * 0.35).toInt()
            plannedcaloriedinner = (RDI.toInt() * 0.3).toInt()
            plannedcaloriesnack = (RDI.toInt() * 0.025).toInt()
            plannedcalorielostfromworkout = (RDI.toInt() * 0.175).toInt()
        }
        else if (dietgoal == "Slow Weight Loss"){
            plannedcaloriebreakfast = (RDI.toInt() * 0.3).toInt()
            plannedcalorielunch = (RDI.toInt() * 0.35).toInt()
            plannedcaloriedinner = (RDI.toInt() * 0.3).toInt()
            plannedcaloriesnack = (RDI.toInt() * 0.05).toInt()
            plannedcalorielostfromworkout = (RDI.toInt() * 0.15).toInt()
        }
        else if (dietgoal == "Maintain Body Weight"){
            plannedcaloriebreakfast = (RDI.toInt() * 0.3).toInt()
            plannedcalorielunch = (RDI.toInt() * 0.35).toInt()
            plannedcaloriedinner = (RDI.toInt() * 0.3).toInt()
            plannedcaloriesnack = (RDI.toInt() * 0.1).toInt()
            plannedcalorielostfromworkout = (RDI.toInt() * 0.125).toInt()
        }
        else if (dietgoal == "Slow Weight Gain"){
            plannedcaloriebreakfast = (RDI.toInt() * 0.3).toInt()
            plannedcalorielunch = (RDI.toInt() * 0.35).toInt()
            plannedcaloriedinner = (RDI.toInt() * 0.35).toInt()
            plannedcaloriesnack = (RDI.toInt() * 0.1).toInt()
            plannedcalorielostfromworkout = (RDI.toInt() * 0.1).toInt()
        }
        else if (dietgoal == "Weight Gain"){
            plannedcaloriebreakfast = (RDI.toInt() * 0.35).toInt()
            plannedcalorielunch = (RDI.toInt() * 0.4).toInt()
            plannedcaloriedinner = (RDI.toInt() * 0.325).toInt()
            plannedcaloriesnack = (RDI.toInt() * 0.1).toInt()
            plannedcalorielostfromworkout = (RDI.toInt() * 0.05).toInt()
        }

        val _btnMeal = view.findViewById<Button>(R.id.btnGetMealPlan)

        val _textBreakfast = view.findViewById<TextView>(R.id.textBreakfastNumber)
        val _textLunch = view.findViewById<TextView>(R.id.textLunchNumber)
        val _textDinner = view.findViewById<TextView>(R.id.textDinnerNumber)
        val _textSnack = view.findViewById<TextView>(R.id.textSnackNumber)
        val _textWorkout = view.findViewById<TextView>(R.id.textWorkoutNumber)

        _btnMeal.setOnClickListener {
            if(status != "1") {
                //throw to buy premium page
                val intentBuyPremium = Intent(activity?.applicationContext, buyPremiumActivity::class.java)
                startActivity(intentBuyPremium)

                Toast.makeText(activity?.applicationContext, "Account is not PREMIUM yet", Toast.LENGTH_SHORT).show()
            }
            else if (dietgoal == "" || RDIuser.toInt() == 0) {
                //throw to calc page
                setSelectedPage(R.id.navigation_profile)
            }
            else {
                _textBreakfast.text = "$plannedcaloriebreakfast kcal"
                _textLunch.text = "$plannedcalorielunch kcal"
                _textDinner.text = "$plannedcaloriedinner kcal"
                _textSnack.text = "$plannedcaloriesnack kcal"

                _textWorkout.text = "$plannedcalorielostfromworkout kcal"

                showAll()
            }

        }
        hideAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        planViewModel =
            ViewModelProvider(this).get(PlanViewModel::class.java)

        _binding = FragmentPlanBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideAll() {
        val _btnMeal = view?.findViewById<Button>(R.id.btnGetMealPlan)


        val _textBreakfastNum = view?.findViewById<TextView>(R.id.textBreakfastNumber)
        val _textLunchNum = view?.findViewById<TextView>(R.id.textLunchNumber)
        val _textDinnerNum = view?.findViewById<TextView>(R.id.textDinnerNumber)
        val _textSnackNum = view?.findViewById<TextView>(R.id.textSnackNumber)
        val _textWorkoutNum = view?.findViewById<TextView>(R.id.textWorkoutNumber)

        val _textBreakfast = view?.findViewById<TextView>(R.id.textBreakfast)
        val _textLunch = view?.findViewById<TextView>(R.id.textLunch)
        val _textDinner = view?.findViewById<TextView>(R.id.textDinner)
        val _textSnack = view?.findViewById<TextView>(R.id.textSnack)
        val _textWorkout = view?.findViewById<TextView>(R.id.textWorkout)

        _btnMeal?.visibility  = View.VISIBLE

        _textBreakfastNum?.visibility  = View.GONE
        _textLunchNum?.visibility  = View.GONE
        _textDinnerNum?.visibility  = View.GONE
        _textSnackNum?.visibility  = View.GONE
        _textWorkoutNum?.visibility  = View.GONE
        _textBreakfast?.visibility  = View.GONE
        _textLunch?.visibility  = View.GONE
        _textDinner?.visibility  = View.GONE
        _textSnack?.visibility  = View.GONE
        _textWorkout?.visibility  = View.GONE

    }

    private fun showAll() {
        val _btnMeal = view?.findViewById<Button>(R.id.btnGetMealPlan)


        val _textBreakfastNum = view?.findViewById<TextView>(R.id.textBreakfastNumber)
        val _textLunchNum = view?.findViewById<TextView>(R.id.textLunchNumber)
        val _textDinnerNum = view?.findViewById<TextView>(R.id.textDinnerNumber)
        val _textSnackNum = view?.findViewById<TextView>(R.id.textSnackNumber)
        val _textWorkoutNum = view?.findViewById<TextView>(R.id.textWorkoutNumber)

        val _textBreakfast = view?.findViewById<TextView>(R.id.textBreakfast)
        val _textLunch = view?.findViewById<TextView>(R.id.textLunch)
        val _textDinner = view?.findViewById<TextView>(R.id.textDinner)
        val _textSnack = view?.findViewById<TextView>(R.id.textSnack)
        val _textWorkout = view?.findViewById<TextView>(R.id.textWorkout)

        _btnMeal?.visibility  = View.VISIBLE

        _textBreakfastNum?.visibility  = View.VISIBLE
        _textLunchNum?.visibility  = View.VISIBLE
        _textDinnerNum?.visibility  = View.VISIBLE
        _textSnackNum?.visibility  = View.VISIBLE
        _textWorkoutNum?.visibility  = View.VISIBLE
        _textBreakfast?.visibility  = View.VISIBLE
        _textLunch?.visibility  = View.VISIBLE
        _textDinner?.visibility  = View.VISIBLE
        _textSnack?.visibility  = View.VISIBLE
        _textWorkout?.visibility  = View.VISIBLE
    }
}