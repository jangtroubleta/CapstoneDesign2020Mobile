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
import com.bumptech.glide.Glide
import com.example.capstonedesign2020mobile.databinding.ItemResumeBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_resume_list.*
import kotlinx.android.synthetic.main.item_resume.view.*
import okhttp3.*
import java.io.IOException

class ResumeListActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_list)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@ResumeListActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val name = intent.getStringExtra("name").toString()
        val title = intent.getStringExtra("title").toString()
        val resumeList = intent.getSerializableExtra("resumeList") as ArrayList<Resume>
        val myIp = intent.getSerializableExtra("myIp") as EditIp

        nav_name.text = name + " 님"
        resume_list_textView.text = title + "\n지원자 현황"

        resumeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResumeListActivity)
            adapter = ResumeAdapter(resumeList) {resume ->
                println("-----------------------------------------------------")
                println(resume.id)

                val url = "http://${myIp.ip}:8000/mobile/services/ResumeDetail.php?id=${resume.id}"
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


                        val intent = Intent(this@ResumeListActivity, ResumeDetailActivity::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("title", title)
                        intent.putExtra("resume", list.result[0])
                        intent.putExtra("myIp", myIp)
                        startActivity(intent)

                    }
                })

            }
        }

    }
    data class JsonObj(val result : List<ResumeDetail>)
}

class ResumeAdapter(val items: List<Resume>,
                    private val clickListener: (resume: Resume)-> Unit) : RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder>() {
    class ResumeViewHolder(val binding: ItemResumeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resume, parent, false)
        val viewHolder = ResumeViewHolder(ItemResumeBinding.bind(view))

        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ResumeViewHolder, position: Int) {
        holder.binding.resume = items[position]
        val myIp = EditIp()

        Glide.with(holder.itemView).load("http://${myIp.ip}:8000/storage/${items[position].image}")
            .into(holder.itemView.resume_list_image)

//        holder.itemView.resume_list_image.background = ShapeDrawable(OvalShape())
//        holder.itemView.resume_list_image.clipToOutline = true

        if (items[position].result == "1") {
            holder.itemView.resume_list_result.text = "합격"
            holder.itemView.resume_list_result.setTextColor(Color.parseColor("#03A9F4"))
        } else if (items[position].result == "2") {
            holder.itemView.resume_list_result.text = "불합격"
            holder.itemView.resume_list_result.setTextColor(Color.parseColor("#F44336"))
        } else {
            holder.itemView.resume_list_result.text = "심사중.."
        }

    }
}