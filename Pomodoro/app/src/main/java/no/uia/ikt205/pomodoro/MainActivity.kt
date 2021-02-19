package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime
import org.w3c.dom.Text
import java.nio.file.Files.size

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var pausedTimer:CountDownTimer
    lateinit var startButton:Button
    lateinit var countdownDisplay:TextView
    lateinit var seekBarStart:SeekBar
    lateinit var seekBarPause:SeekBar
    lateinit var isPausedText:TextView
    lateinit var repetitionNumber:EditText
    lateinit var workTimeDuration:TextView
    lateinit var pauseTimeDuration:TextView
    lateinit var stopButton:Button

    var isTimerStarted = false
    var isPauseStarted = false

    var numberOfRepetition:Int = 0

    var timeToCountDownInMs = 15 * 60000L
    var timeTicks = 1000L
    var pausedTime = 0L
    var workedTime = 15 * 60000L
    var timeStopped = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Time displays for worktime and pausetime
        workTimeDuration = findViewById<TextView>(R.id.workTimeDuration)
        pauseTimeDuration = findViewById<TextView>(R.id.pauseTimeDuration)

        //Added text so the user can see if the timer is showing worktime or pausetime.
        isPausedText = findViewById(R.id.isPausedText)
        isPausedText.visibility = View.INVISIBLE

        seekBarStart = findViewById<SeekBar>(R.id.seekBarStart)
        seekBarStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isTimerStarted) {
                    timer.cancel()
                    isTimerStarted = false
                }
                timeToCountDownInMs = progress * 60000L
                workedTime = progress * 60000L

                updateCountDownDisplay(timeToCountDownInMs)
                updateWorkTimeDuration(workedTime)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(this@MainActivity, "Not yet implemented", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(this@MainActivity, "Current time selected is ${seekBarStart.progress}", Toast.LENGTH_SHORT).show()
            }
        })


        seekBarPause = findViewById<SeekBar>(R.id.seekBarPause)
        seekBarPause.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean){
                pausedTime = progress * 60000L
                updatePauseTimeDuration(pausedTime)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(this@MainActivity, "Not yet implemented", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(this@MainActivity, "Current time selected is ${seekBarPause.progress}", Toast.LENGTH_SHORT).show()
            }
        })
        repetitionNumber = findViewById<EditText>(R.id.repetitionText)

        stopButton = findViewById<Button>(R.id.stopCountdownButton)
        stopButton.setOnClickListener(){
            if (isTimerStarted){
                timer.cancel()
                timeToCountDownInMs = timeStopped;
                updateCountDownDisplay(timeToCountDownInMs)
                isTimerStarted = false;
                isPausedText.visibility = View.INVISIBLE

            }else {
                Toast.makeText(this@MainActivity, "Timer is not started!", Toast.LENGTH_SHORT).show()
            }
        }

       startButton = findViewById<Button>(R.id.startCountdownButton)
       startButton.setOnClickListener(){
           if (!isTimerStarted){
               startCountDown(it)
               numberOfRepetition = repetitionNumber.text.toString().toInt()
               isTimerStarted = true
           }
       }
       countdownDisplay = findViewById<TextView>(R.id.countDownView)

    }

    fun startCountDown(v: View){
        if (isTimerStarted) {
            Toast.makeText(this@MainActivity, "Timer is already started!", Toast.LENGTH_SHORT).show()
            timer.cancel()
            isTimerStarted = false
            return
        }

        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                Toast.makeText(this@MainActivity,"Arbeidsøkt er ferdig", Toast.LENGTH_SHORT).show()
                isTimerStarted = false
                numberOfRepetition--
                repetitionNumber.setText(numberOfRepetition.toString())

                if (numberOfRepetition >= 0){
                    Toast.makeText(this@MainActivity, "Take a break!", Toast.LENGTH_SHORT).show()
                    startPauseCountDown(v)
                }
            }

            override fun onTick(millisUntilFinished: Long) {
               updateCountDownDisplay(millisUntilFinished)
            }
        }

        timer.start()
        isTimerStarted = true

    }

    fun startPauseCountDown(v: View){
        pausedTimer = object : CountDownTimer(pausedTime, timeTicks){
            override fun onFinish(){
                Toast.makeText(this@MainActivity, "Break is over, back to work!", Toast.LENGTH_SHORT).show()
                isPauseStarted = false

                numberOfRepetition = repetitionNumber.text.toString().toInt()
                if (numberOfRepetition >= 0){
                    Toast.makeText(this@MainActivity, "Take a break!", Toast.LENGTH_SHORT).show()
                    timer.start()
                }else {
                    timer.cancel()
                }

                isPausedText.visibility = View.INVISIBLE
            }

            override fun onTick(millisUntilFinished: Long){
                isPausedText.visibility = View.VISIBLE
                updateCountDownDisplay(millisUntilFinished)
            }
        }
        pausedTimer.start()
        isPauseStarted = true
    }

    private fun startPausedTimer(v: View) {
        if (isPauseStarted){
            pausedTimer.cancel()
            isPauseStarted = false
        }

    }


    fun updateCountDownDisplay(timeInMs:Long){
        countdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

    fun updateWorkTimeDuration(timeInMs: Long){
        workTimeDuration.text = millisecondsToDescriptiveTime(timeInMs)
    }

    fun updatePauseTimeDuration(timeInMs: Long){
        pauseTimeDuration.text = millisecondsToDescriptiveTime(timeInMs)
    }
}

