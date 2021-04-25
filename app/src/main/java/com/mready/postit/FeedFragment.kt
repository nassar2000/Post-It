package com.mready.postit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mready.postit.Adapter.AdapterForHomePage
import com.mready.postit.helper.Constants
import com.mready.postit.helper.SharedPrefManager
import com.mready.postit.model.MessageModel
import com.mready.postit.ui.auth.AddNewPostFragment
import com.mready.postit.ui.auth.RegisterActivity
import kotlinx.android.synthetic.main.action_bar_set.*
import kotlinx.android.synthetic.main.action_bar_set.view.*
import kotlinx.android.synthetic.main.fragment_feed.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment() {
    private var itemAdapter: AdapterForHomePage? = null

    var messageList = arrayListOf<MessageModel>()

    companion object {
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()

        itemAdapter = AdapterForHomePage(context ?: return, messageList)

        recyclerView_messagesShow.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        getMessage()
        button_AddPost.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(
                    R.id.frameLayout_homePage,
                    AddNewPostFragment.newInstance(),
                    AddNewPostFragment.toString()
                )
                ?.addToBackStack(getString(R.string.feedFragment))
                ?.commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.item_logOut -> {
                requireActivity().run {
                    SharedPrefManager(this).logout()
                    startActivity(Intent(this, RegisterActivity::class.java))
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getMessage() {
        val queue = Volley.newRequestQueue(context)
        val url = Constants.MessageRoute

        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
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
                    val jsonData = obj.getJSONArray(Constants.JsonData)
                    for (i in 0 until jsonData.length()) {
                        val jsonObject = jsonData.getJSONObject(i)
                        val displayName = jsonObject.getString(Constants.DisplayName)
                        val message = jsonObject.getString(Constants.JasonMessage)
                        val createdAt = changeFormatDate(jsonObject.getString(Constants.CreatedAt))


                        val objectMessage = MessageModel(
                            displayName,
                            message,
                            createdAt ?: return@Listener
                        )
                        messageList.add(objectMessage)
                    }

                    itemAdapter?.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Log.d(Constants.TagApi, "$error")
                }

            ) {}
        queue.add(stringReq)
    }


    fun changeFormatDate(time: String?): String? {
        val inputPattern = getString(R.string.inputDtatTime)
        val outputPattern = getString(R.string.outputDtatTime)
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        val date: Date?
        var str: String? = null
        try {
            date = inputFormat.parse(time ?: return null)
            str = outputFormat.format(date ?: return null)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    private fun setActionBar() {
        val viewActionBar: View =
            layoutInflater.inflate(R.layout.action_bar_set, null as ViewGroup?)
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        viewActionBar.textView_TitleActionBar.text = Constants.ActionBarTitleFeed
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).supportActionBar?.setCustomView(viewActionBar, params)
    }


}
