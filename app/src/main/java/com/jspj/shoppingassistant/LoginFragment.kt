package com.jspj.shoppingassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jspj.shoppingassistant.databinding.FragmentLoginBinding
import android.graphics.Paint;
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var auth:FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Init();
        navController= Navigation.findNavController(view)
        binding.linkSignUp.setOnClickListener{
            navController.navigate(R.id.action_loginFragment_to_signupFragment)

        }
        binding.btnLogin.setOnClickListener{
            val user = binding.txtUsername.text.toString();
            val pass = binding.txtPassword.text.toString();
            LoginUser(user,pass);
        }
    }

    private fun Init(){
        var tw = binding.linkSignUp;
        tw.paintFlags=tw.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        auth=FirebaseAuth.getInstance();
    }

    private fun Validate()
    {
        //TODO: Dodati validaciju!
    }

    private fun LoginUser(email:String,pass:String)
    {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful)
                navController.navigate(R.id.action_loginFragment_to_mainMenuFragment)
            else
                Toast.makeText(context, R.string.tst_error_log, Toast.LENGTH_SHORT).show()

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
            }
    }
}