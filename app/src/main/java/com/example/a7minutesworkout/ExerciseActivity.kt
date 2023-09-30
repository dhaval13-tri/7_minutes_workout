package com.example.a7minutesworkout

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding ?= null

    private var restTimer: CountDownTimer ?= null
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer ?= null
    private var exerciseProgress = 0

    private var exerciseTimerDuration:Long = 30
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech ?= null

    private var player: MediaPlayer ?= null
    private var exerciseAdapter: ExerciseStatusAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setUpRestView()
        setUpExerciseStatusRecyclerView()

    }

    private fun setUpExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(
            this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setUpRestView(){

        try {
            val soundUri = Uri.parse("android.resource://com.example.a7minutesworkout/" +R.raw.exercise_music)
            player = MediaPlayer.create(applicationContext,soundUri)
            player?.isLooping = false
            player?.start()

        }catch (e: Exception){
            e.printStackTrace()
        }


        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE



        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()


        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        speakOut("Upcoming Exercise: " + exerciseList!![currentExercisePosition+1].getName())
        setRestProgressBar()
    }

    private fun setUpExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.text = getString(R.string.exercise_name)
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut("Start:  " +exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress
        restTimer = object : CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged()

                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setUpRestView()
                } else {
                    player?.stop()
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have completed the 7 minutes workout.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        if (player != null){
            player?.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","language not supported")
            }
            else{
                Log.e("TTS","Initialization failed")
            }
        }
    }

   private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}