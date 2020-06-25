package com.example.capstonedesign2020mobile

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_feedback_detail.*
import okhttp3.*
import java.io.IOException

class FeedbackDetailActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_detail)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@FeedbackDetailActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val id = intent.getStringExtra("id")
        val userid = intent.getStringExtra("userid").toString()
        val name = intent.getStringExtra("name").toString()
        val expert_id = intent.getStringExtra("expert_id")
        val feedback = intent.getSerializableExtra("feedback") as FeedbackDetail
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name.text = name + " 님"

        println(feedback)

        fb_detail_title.text = feedback.title
        Glide.with(this).load("http://${myIp.ip}:8000/storage/${feedback.image}")
            .into(fb_detail_image)
        fb_detail_image.background = ShapeDrawable(OvalShape())
        fb_detail_image.clipToOutline = true
        fb_detail_name.text = feedback.name
        fb_detail_date.text = feedback.date
        fb_detail_video.setVideoPath("http://${myIp.ip}:8000/mobile/videos/feedback_sample_data.mp4")
        var mediaController: MediaController = MediaController(this)
        mediaController.setAnchorView(fb_detail_video)
        fb_detail_video.setMediaController(mediaController)
        fb_detail_video.setOnErrorListener(MediaPlayer.OnErrorListener { arg0, arg1, arg2 -> // 예외처리 로직 ....
            true
        })
        fb_detail_content.text = feedback.content

        if (feedback.answer == null) {
            fb_detail_edit_card1.visibility = View.VISIBLE
            fb_detail_answer_card.visibility = View.GONE
            fb_detail_edit_card2.visibility = View.GONE

        } else {
            fb_detail_edit_card1.visibility = View.GONE
            fb_detail_answer_card.visibility = View.VISIBLE
            fb_detail_edit_card2.visibility = View.GONE

            fb_detail_answer.text = feedback.answer

        }

        fb_detail_button1.setOnClickListener {
            //피드백 작성하기
            fb_detail_edit_card1.visibility = View.GONE
            fb_detail_editText.text = null
            fb_detail_edit_card2.visibility = View.VISIBLE

        }

        fb_detail_button2.setOnClickListener {
            //수정하기
            fb_detail_answer_card.visibility = View.GONE
            fb_detail_editText.text = null
            fb_detail_editText.setText(feedback.answer)
            fb_detail_edit_card2.visibility = View.VISIBLE
        }

        fb_detail_button3.setOnClickListener {
            //저장하기
            val fbList = arrayListOf<Feedback>()

            println(fb_detail_editText.text)
            val url = "http://${myIp.ip}:8000/mobile/services/FeedbackDetail2.php?article_id=${feedback.id}&expert_id=${expert_id}&answer=\"${fb_detail_editText.text}\""
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
                        fbList.add(Feedback(res.id, res.name, res.title, res.date, res.answer))
                        println(fbList)
                    }

                    val intent = Intent(this@FeedbackDetailActivity, FeedbackListActivity::class.java)
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

    }
    data class JsonObj(val result : List<Feedback>)
}
