package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime

class MainActivity  : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var button30:Button
    lateinit var button60:Button
    lateinit var button90:Button
    lateinit var button120:Button
    lateinit var startButton:Button
    lateinit var stopButton:Button


    lateinit var coutdownDisplay:TextView

    var timeToCountDownInMs = 5000L
    val timeTicks = 1000L
    var timeStopped = 0L

    var isTimeStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button30 = findViewById<Button>(R.id.startCountdownButton30)
        button60 = findViewById<Button>(R.id.startCountdownButton60)
        button90 = findViewById<Button>(R.id.startCountdownButton90)
        button120 = findViewById<Button>(R.id.startCountdownButton120)



        button30.setOnClickListener(){
            if (!isTimeStarted){
                timeToCountDownInMs = 30 * 60 * 1000;
                updateCountDownDisplay(timeToCountDownInMs)
            }

        }
        button60.setOnClickListener(){
            if (!isTimeStarted){
                timeToCountDownInMs = 60 * 60 * 1000;
                updateCountDownDisplay(timeToCountDownInMs)
            }

        }
        button90.setOnClickListener(){
            if (!isTimeStarted){
                timeToCountDownInMs = 90 * 60 * 1000;
                updateCountDownDisplay(timeToCountDownInMs)
            }

        }
        button120.setOnClickListener(){
            if (!isTimeStarted){
                timeToCountDownInMs = 120 * 60 * 1000;
                updateCountDownDisplay(timeToCountDownInMs)
            }

        }

       startButton = findViewById<Button>(R.id.startCountdownButton)
       startButton.setOnClickListener(){
           if (isTimeStarted){
               Toast.makeText(this@MainActivity, "Timer is running", Toast.LENGTH_SHORT).show()

           }else {
                startCountDown(it)

               isTimeStarted = true;
           }
       }

        stopButton = findViewById<Button>(R.id.stopCountdownButton)
        stopButton.setOnClickListener(){
            if (isTimeStarted){
                timer.cancel()
                timeToCountDownInMs = timeStopped;
                updateCountDownDisplay(timeToCountDownInMs)
                isTimeStarted = false;

            }else {
                Toast.makeText(this@MainActivity, "Timer is not started!", Toast.LENGTH_SHORT).show()
            }
        }
        coutdownDisplay = findViewById<TextView>(R.id.countDownView)

    }

    fun startCountDown(v: View){

        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"Time is up!", Toast.LENGTH_SHORT).show()
                updateCountDownDisplay(timeToCountDownInMs)
                isTimeStarted = false
            }

            override fun onTick(millisUntilFinished: Long) {
               updateCountDownDisplay(millisUntilFinished)
                timeToCountDownInMs = millisUntilFinished
            }
        }

        timer.start()
    }


    fun updateCountDownDisplay(timeInMs:Long){
        coutdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

}