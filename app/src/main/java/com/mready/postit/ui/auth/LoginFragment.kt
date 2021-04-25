package com.mready.postit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mready.postit.MainActivity
import com.mready.postit.R
import com.mready.postit.helper.Constants
import com.mready.postit.helper.SharedPrefManager
import com.mready.postit.model.User
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment() {

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView_CreateAccount.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.frameLayout_auth,
                    RegisterFragment.newInstance(),
                    RegisterFragment.toString()
                )
                ?.addToBackStack(getString(R.string.LoginFragment))
                ?.commit()
        }

        button_Login.setOnClickListener {
            verificationDataAndSubmit()
        }
    }

    private fun login(username: String, password: String) {
        val queue = Volley.newRequestQueue(context)
        val url = Constants.LoginRoute

        val stringReq: StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val obj = JSONObject(response)

                    try {
                        // response

                        val jsonError = obj.getJSONObject(Constants.JasonError)
                        val jsonMessageError = jsonError.getString(Constants.JasonMessage)

                        Toast.makeText(context, jsonMessageError, Toast.LENGTH_LONG).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    try {

                        val jsonData = obj.getJSONObject(Constants.JsonData)
                        val jsonUser = jsonData.getJSONObject(Constants.JsonUser)
                        val jsonToken = jsonData.getString(Constants.JsonToken)

                        val user = User(
                            jsonUser.getInt(Constants.Id).toString().toInt(),
                            jsonUser.getString(Constants.Username),
                            jsonUser.getString(Constants.DisplayName),
                            jsonUser.getString(Constants.CreatedBy),
                            jsonToken
                        )

                        SharedPrefManager(context ?: return@Listener).userLogin(user)
                        requireActivity().run {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(Constants.TagApi, "$error")
                }


            ) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params[Constants.Username] = username
                    params[Constants.Password] = password
                    return params
                }

            }
        queue.add(stringReq)
    }

    private fun verificationDataAndSubmit() {

        val userName = textView_Username.text.toString()
        val password = textView_password.text.toString()

        when {
            userName.isEmpty() -> editText_userName.error = Constants.EmptyError
            password.isEmpty() -> editText_Password.error = Constants.EmptyError

            else -> {

                login(textView_Username.text.toString(), textView_password.text.toString())


            }
        }


    }
}


