package com.example.capstonedesign2020mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val id = intent.getStringExtra("id").toString()
        val userid = intent.getStringExtra("userid").toString()
        val name = intent.getStringExtra("name").toString()
        val port = intent.getStringExtra("port").toString()
        val expert_id = intent.getStringExtra("expert_id")
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name.text = name + " 님"

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        if (port == "1" || port == "2") {
            card_myAudition.visibility = View.GONE
            card_myFeedback.visibility = View.VISIBLE

            val fbList = arrayListOf<Feedback>()

            card_myFeedback.setOnClickListener {

                val url = "http://${myIp.ip}:8000/mobile/services/FeedbackList.php?expert_id=$expert_id"
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
                        val list = gson.fromJson(body, FeedbackJsonObj::class.java)

                        println(list.result)
                        println("----------------------")
                        for (res in list.result) {
                            fbList.add(Feedback(res.id, res.name, res.title, res.date, res.answer))
                            println(fbList)
                        }

                        val intent = Intent(this@MainActivity, FeedbackListActivity::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("userid", userid)
                        intent.putExtra("name", name)
                        intent.putExtra("expert_id", expert_id)
                        intent.putExtra("fbList", fbList)
                        intent.putExtra("myIp", myIp)
                        startActivity(intent)
                        finish()
                    }
                })

            }

        } else if (port == "3") {
            card_myAudition.visibility = View.VISIBLE
            card_myFeedback.visibility = View.GONE

            val audlist = arrayListOf<Audition>()

            card_myAudition.setOnClickListener {

                val url = "http://${myIp.ip}:8000/mobile/services/AuditionList.php?id=$id"
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
                        val list = gson.fromJson(body, AudJsonObj::class.java)

                        println(list.result)
                        println("----------------------")
                        for (res in list.result) {
                            audlist.add(Audition(res.id, res.aud_image, res.title, res.end_date))
                            println(audlist)
                        }

                        val intent = Intent(this@MainActivity, AuditionListActivity::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("userid", userid)
                        intent.putExtra("name", name)
                        intent.putExtra("audlist", audlist)
                        intent.putExtra("myIp", myIp)
                        startActivity(intent)
                        finish()
                    }
                })

            }

        }
    }
    data class AudJsonObj(val result : List<Audition>)
    data class FeedbackJsonObj(val result : List<Feedback>)
}
