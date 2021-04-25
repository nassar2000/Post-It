package com.mready.postit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
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
import kotlinx.android.synthetic.main.action_bar_set.*
import kotlinx.android.synthetic.main.action_bar_set.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONException
import org.json.JSONObject


class RegisterFragment : Fragment() {


    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentManager?.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()

        button_register.setOnClickListener {
            verificationDataAndSubmit()
        }
    }

    private fun register(username: String, password: String, displayName: String) {
        val queue = Volley.newRequestQueue(context)
        val url = Constants.RegisterRoute

        val stringReq: StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    val obj = JSONObject(response)

                    try {
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
                }, Response.ErrorListener { error ->
                    Log.d(Constants.TagApi, "$error")
                }
            ) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params[Constants.Username] = username
                    params[Constants.Password] = password
                    params[Constants.DisplayName] = displayName
                    return params
                }

            }
        queue.add(stringReq)
    }

    private fun verificationDataAndSubmit() {

        val fullName = editText_fullName.text.toString()
        val userName = editText_userName_register.text.toString()
        val password = editText_Password_register.text.toString()
        val repeatPassword = editText_RepeatPassword_register.text.toString()

        when {
            fullName.isEmpty() -> editText_fullName.error = Constants.EmptyError
            userName.isEmpty() -> editText_userName_register.error = Constants.EmptyError
            password.isEmpty() -> editText_Password_register.error = Constants.EmptyError
            repeatPassword.isEmpty() -> editText_RepeatPassword_register.error = Constants.EmptyError
            password.length < 6 -> editText_Password_register.error = Constants.PasswordLength
            password != repeatPassword -> editText_Password_register.error = Constants.MatchingPasswordError


            else -> {

                register(
                    editText_userName_register.text.toString(),
                    editText_Password_register.text.toString(),
                    editText_fullName.text.toString()
                )


            }
        }
    }

    private fun setActionBar() {
        val viewActionBar: View = layoutInflater.inflate(
            R.layout.action_bar_set,
            null as ViewGroup?
        )
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        viewActionBar.textView_TitleActionBar.text = Constants.ActionBarTitleRegister
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setCustomView(viewActionBar, params)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
    }

}