package com.example.capstonedesign2020mobile

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonedesign2020mobile.databinding.ItemFeedbackBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_feedback_list.*
import kotlinx.android.synthetic.main.item_feedback.view.*
import okhttp3.*
import java.io.IOException

class FeedbackListActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_list)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@FeedbackListActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val id = intent.getStringExtra("id")
        val userid = intent.getStringExtra("userid").toString()
        val name = intent.getStringExtra("name").toString()
        val expert_id = intent.getStringExtra("expert_id")
        val fbList = intent.getSerializableExtra("fbList") as ArrayList<Feedback>
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name.text = name + " 님"
        fb_list_textView.text = "$name 님의 피드백 관리 페이지 입니다."

       fbRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FeedbackListActivity)
            adapter = FeedbackAdapter(fbList) {feedback ->
                println("-----------------------------------------------------")
                println(feedback)
                var fbId = feedback.id
                val url = "http://${myIp.ip}:8000/mobile/services/FeedbackDetail.php?id=$fbId"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("실패함~~~~~~~~~~~~~~~~~~~~~~~")
                        println(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println("성공함~~~~~~~~~~~~~~~~~~")
                        val body = response?.body()?.string()
                        //Gson으로 파싱
                        val gson = GsonBuilder().create()
                        val list = gson.fromJson(body, JsonObj::class.java)

                        println(list.result)
                        println("----------------------")
                        val intent = Intent(this@FeedbackListActivity, FeedbackDetailActivity::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("userid", userid)
                        intent.putExtra("name", name)
                        intent.putExtra("expert_id", expert_id)
                        intent.putExtra("feedback", list.result[0])
                        intent.putExtra("myIp", myIp)
                        startActivity(intent)

                    }
                })

            }

        }

    }
    data class JsonObj(val result : List<FeedbackDetail>)
}

class FeedbackAdapter(val items: List<Feedback>, private val clickListener: (feedback: Feedback)-> Unit) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    class FeedbackViewHolder(val binding: ItemFeedbackBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feedback, parent, false)
        val viewHolder = FeedbackViewHolder(ItemFeedbackBinding.bind(view))

        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        if (items[position].answer == null) {
            holder.itemView.fb_check.text = "미답변"
            holder.itemView.fb_check.setTextColor(Color.parseColor("#F44336"))
        } else {
            holder.itemView.fb_check.text = "답변완료"
            holder.itemView.fb_check.setTextColor(Color.parseColor("#03A9F4"))
        }

        holder.binding.feedback = items[position]
    }
}
