package com.example.capstonedesign2020mobile

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_audition_detail.*
import okhttp3.*
import java.io.IOException

class AuditionDetailActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audition_detail)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@AuditionDetailActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val id = intent.getStringExtra("id").toString()
        val title = intent.getStringExtra("title").toString()
        val aud_image = intent.getStringExtra("aud_image").toString()
        val rank = intent.getStringExtra("rank").toString()
        val end_date = intent.getStringExtra("end_date").toString()
        val name = intent.getStringExtra("name").toString()
        val call_number = intent.getStringExtra("call_number").toString()
        val company_name = intent.getStringExtra("company_name").toString()
        val address = intent.getStringExtra("address").toString()
        val content = intent.getStringExtra("content").toString()
        val video = intent.getStringExtra("video").toString()
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name.text = name + " 님"

        val resumeList = arrayListOf<Resume>()

        aud_detail_title.text = title
        Glide.with(this).load("http://${myIp.ip}:8000/storage/$aud_image")
            .into(aud_detail_image)
        println(aud_image)
        aud_detail_rank.text = rank
        aud_detail_date.text = end_date
        aud_detail_name.text = name
        aud_detail_call_number.text = call_number
        aud_detail_company.text = company_name
        aud_detail_address.text = address
        aud_detail_content.text = content

        aud_detail_video.setVideoPath("http://${myIp.ip}:8000/videos/$video.mp4")
        var mediaController: MediaController = MediaController(this)
        mediaController.setAnchorView(aud_detail_video)
        aud_detail_video.setMediaController(mediaController)
        aud_detail_video.setOnErrorListener(MediaPlayer.OnErrorListener { arg0, arg1, arg2 -> // 예외처리 로직 ....
            true
        })
        //aud_detail_video.start()

        aud_detail_button.setOnClickListener {

            val url = "http://${myIp.ip}:8000/mobile/services/ResumeList.php?id=$id"
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

                    val intent = Intent(this@AuditionDetailActivity, ResumeListActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("title", title)
                    intent.putExtra("resumeList", resumeList)
                    intent.putExtra("myIp", myIp)
                    startActivity(intent)
                    finish()
                }
            })
        }

    }
    data class JsonObj(val result : List<Resume>)

}
