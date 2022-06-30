package com.example.easyfit

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.easyfit.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFr.newInstance] factory method to
 * create an instance of this fragment.
 */

lateinit var db : FirebaseFirestore
var loggedInUser = ""
var name = ""
var RDIuser = ""
var status:String = ""

class LoginFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        //reset logged in user
        loggedInUser = ""

        val _editUsername = view.findViewById<EditText>(R.id.username)
        val _editPassword = view.findViewById<EditText>(R.id.password)
        val _btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val _textSignUp = view.findViewById<TextView>(R.id.textSignUp)
        val _checkbocxRememberPass = view.findViewById<CheckBox>(R.id.checkboxRememberPass)

        val loginPref = this.requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val prefEditor = loginPref.edit()
        val saveLogin = loginPref.getBoolean("saveLogin", false)

        if (saveLogin) {
            _editUsername.setText(loginPref.getString("username", ""))
            _editPassword.setText(loginPref.getString("password", ""))
            _checkbocxRememberPass.isChecked = true
        }

        _btnLogin.setOnClickListener {
            val usernameKey = _editUsername.text.toString()
            val passwordKey = _editPassword.text.toString()

            if(_editUsername.text.isNotBlank() && _editPassword.text.isNotBlank()) {
                //remember username and password
                if (_checkbocxRememberPass.isChecked) {
                    prefEditor.putBoolean("saveLogin", true);
                    prefEditor.putString("username", usernameKey);
                    prefEditor.putString("password", passwordKey);
                    prefEditor.apply();
                } else {
                    prefEditor.clear();
                    prefEditor.apply();
                }

                //check username and password
                db.collection("dataLogin")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (usernameKey == document.data["username"].toString() && passwordKey == document.data["password"].toString()) {
                                //login successful
                                loggedInUser = document.data["username"].toString()
                                name = document.data["name"].toString()
                                RDIuser = document.data["rdi"].toString()
                                dietgoal = document.data["dietgoal"].toString()
                                status = document.data["status"].toString()

                                //pindah page dari login ke home
                                view.findNavController().navigate(R.id.action_loginFr_to_homeAct)

                                Toast.makeText(activity?.applicationContext, "Welcome back, $name", Toast.LENGTH_SHORT).show()
                                break
                            }
                            else {
                                //jika login gagal
                                Toast.makeText(activity?.applicationContext, "Username and Password does not match", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error getting documents: ", exception)
                    }
            }
            else {
                Toast.makeText(activity?.applicationContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        _textSignUp.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFr_to_signUp)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided par ameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}