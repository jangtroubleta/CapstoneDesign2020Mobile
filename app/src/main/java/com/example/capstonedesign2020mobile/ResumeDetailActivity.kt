package com.example.capstonedesign2020mobile

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_resume_detail.*
import okhttp3.*
import java.io.IOException

class ResumeDetailActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_detail)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@ResumeDetailActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val resume = intent.getSerializableExtra("resume") as ResumeDetail
        val title = intent.getStringExtra("title").toString()
        val name = intent.getStringExtra("name").toString()

        val resumeList = arrayListOf<Resume>()
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name2.text = name + " 님"

        resume_detail_title.text = resume.name + " Profile"
        Glide.with(this).load("http://${myIp.ip}:8000/storage/${resume.image}")
            .into(resume_detail_image)
        resume_detail_rank.text = resume.rank_name
        resume_detail_score.text = resume.score
        resume_detail_name.text = resume.name
        resume_detail_userid.text = resume.userid
        if (resume.gender == "0") {
            resume_detail_gender.text = "남"
        } else if (resume.gender == "1") {
            resume_detail_gender.text = "여"
        }
        resume_detail_address.text = resume.address
        resume_detail_call_number.text = resume.call_number
        resume_detail_message.text = resume.message
        if (resume.result == "1") {
            resume_detail_result.text = "합격"
            resume_detail_result.setTextColor(Color.parseColor("#03A9F4"))
        } else if (resume.result == "2") {
            resume_detail_result.text = "불합격"
            resume_detail_result.setTextColor(Color.parseColor("#F44336"))
        } else {
            resume_detail_result.text = "심사중.."
        }

        fun postResume(result: String) {
            val url = "http://${myIp.ip}:8000/mobile/services/ResumeDetail2.php?id=${resume.id}&res=${result}&audition_id=${resume.audition_id}"
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
                    for (res in list.result) {
                        resumeList.add(Resume(res.id, res.image, res.userid, res.name, res.result))
                        println(resumeList)
                    }

                    val intent = Intent(this@ResumeDetailActivity, ResumeListActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("title", title)
                    intent.putExtra("resumeList", resumeList)
                    intent.putExtra("myIp", myIp)
                    startActivity(intent)

                }
            })
        }

        resume_detail_button_0.setOnClickListener {
            postResume("0")
        }

        resume_detail_button_1.setOnClickListener {
            postResume("1")
        }

        resume_detail_button_2.setOnClickListener {
            postResume("2")
        }

    }
    data class JsonObj(val result : List<Resume>)
}
