package com.mready.postit.ui.auth

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
import com.mready.postit.FeedFragment
import com.mready.postit.R
import com.mready.postit.helper.Constants
import com.mready.postit.helper.SharedPrefManager
import kotlinx.android.synthetic.main.action_bar_set.view.*
import kotlinx.android.synthetic.main.fragment_add_new_post.*
import org.json.JSONException
import org.json.JSONObject

class AddNewPostFragment : Fragment() {

    companion object {

        fun newInstance(): AddNewPostFragment {
            return AddNewPostFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_new_post, container, false)
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
        val getToken: String? = SharedPrefManager(context ?: return).getToken()

        setActionBar()

        button_sendMessage.setOnClickListener {
            postMessage(
                editText_EnterMessage.text.toString(),
                getToken ?: return@setOnClickListener
            )

        }
    }

    private fun postMessage(message: String, token: String) {
        val queue = Volley.newRequestQueue(context)
        val url = Constants.MessageRoute


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

                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(
                            R.id.frameLayout_homePage,
                            FeedFragment.newInstance(),
                            FeedFragment.toString()
                        )
                        ?.commit()

                },
                Response.ErrorListener { error ->
                    Log.d(Constants.TagApi, "$error")
                }

            ) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params[Constants.JasonMessage] = message
                    params[Constants.JsonToken] = token

                    return params
                }

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params[getString(R.string.authorization)] = "${Constants.Bearer} $token"
                    return params
                }

            }
        queue.add(stringReq)
    }

    private fun setActionBar() {
        val viewActionBar: View =
            layoutInflater.inflate(R.layout.action_bar_set, null as ViewGroup?)
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        viewActionBar.textView_TitleActionBar.text = Constants.ActionBarTitleAddPost
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setCustomView(viewActionBar, params)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(true)
    }


}