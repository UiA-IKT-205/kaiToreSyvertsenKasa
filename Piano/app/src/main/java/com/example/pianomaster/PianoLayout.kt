package com.example.pianomaster

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.pianomaster.databinding.FragmentPianoBinding
import data.Note
import kotlinx.android.synthetic.main.fragment_piano.*
import kotlinx.android.synthetic.main.fragment_piano.view.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PianoLayout : Fragment() {

    var onSave:((file:Uri) -> Unit)? = null

    private var _binding: FragmentPianoBinding? = null
    private val binding get() = _binding!!

    //Creating a list of Full Notes
    private val fullTones = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2", "A2", "B2")
    //Creating a list of Half Notes
    private val halfTones = listOf("C#", "D#", "F#", "G#", "A#", "C#2", "D#2", "F#2", "G#2", "A#2")

    private var noteSheet:MutableList<Note> = mutableListOf<Note>() // MutableList, not to be confused with List.

    private var activeRecording:Boolean = false // To keep track of active recording in if's, when's and else's.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPianoBinding.inflate(layoutInflater)
        val view = binding.root

        val childFragmentManager = childFragmentManager
        val fragmentTransaction = childFragmentManager.beginTransaction()

                //Makes a key for each tone in Full Tones.
                fullTones.forEach {
                    val fullTonePianoKey = FullTonePianoKeyFragment.newInstance(it)

                    // Variables to track time played.
                    var startPlay:Long = 0
                    var fullToneStartTime = ""

                    // Variables to track time played.
                    fullTonePianoKey.onKeyDown = { note ->
                        startPlay = System.nanoTime()
                        val currentTime = LocalDateTime.now()
                        fullToneStartTime = currentTime.format(DateTimeFormatter.ISO_TIME)
                        println("Piano key down $note at $fullToneStartTime")

                    }

                    // Uses the device time instead, so time is accurate.
                    fullTonePianoKey.onKeyUp = {
                        val endPlay = System.nanoTime()
                        var fullToneTotalTime:Double
                        var fullToneTime: Long
                        fullToneTime = endPlay - startPlay
                        fullToneTotalTime = fullToneTime.toDouble() / 1000000000 // Converts nano to seconds
                        val note = Note(it, fullToneStartTime, fullToneTotalTime)
                        noteSheet.add(note)
                        println("Piano key up $note, Started $fullToneStartTime Lasted $fullToneTotalTime, RAW nano = $fullToneTime and seconds = $fullToneTotalTime")

                    }
                    fragmentTransaction.add(view.fullToneKeyLayout.id, fullTonePianoKey, "note_$it")
                }
                //Makes a key for each tone in Half Tones.
                halfTones.forEach {
                    val halfTonePianoKey = HalfTonePianoKeyFragment.newInstance(it)

                    // Variables to track time played.
                    var startPlay:Long = 0
                    var fullToneStartTime = ""

                    // Starts recording time on key pressed.
                    halfTonePianoKey.onKeyDown = {  note ->
                        startPlay = System.nanoTime()
                        val currentTime = LocalDateTime.now()
                        fullToneStartTime = currentTime.format(DateTimeFormatter.ISO_TIME)
                        println("Piano key down $note at $fullToneStartTime")
                    }

                    // Uses the device time instead, so time is accurate.
                    halfTonePianoKey.onKeyUp = {
                        val endPlay = System.nanoTime()
                        var fullToneTotalTime:Double
                        var fullToneTime:Long
                        fullToneTime = endPlay - startPlay
                        fullToneTotalTime = fullToneTime.toDouble() / 1000000000 // Converts nano to seconds
                        val note = Note(it, fullToneStartTime, fullToneTotalTime)
                        noteSheet.add(note)
                        println("Piano key up $note, Started $fullToneStartTime Lasted $fullToneTotalTime, RAW nano = $fullToneTime and seconds = $fullToneTotalTime")
                    }
                    fragmentTransaction.add(view.halfToneKeyLayout.id, halfTonePianoKey, "note_$it")
        }
        fragmentTransaction.commit()

        // Uses a record button to save notes from start to end.
        view.recordButton.setOnClickListener{
            if(!activeRecording){
                noteSheet.clear()
                startRecordTime()
                recordButton.text = "Stop Recording"
            } else {
                stopRecordTime()
                recordButton.text = "Reset Recording"
            }
        }
        view.saveScoreBt.setOnClickListener {
            var fileName = view.fileNameTextEdit.text.toString()
            val path = this.activity?.getExternalFilesDir(null)


            when {
                noteSheet.count() == 0 -> Toast.makeText(activity, "Forgot notes? Did you click by mistake?", Toast.LENGTH_SHORT).show()
                fileName.isEmpty() -> Toast.makeText(activity, "Forgot filename?", Toast.LENGTH_SHORT).show()
                path == null -> Toast.makeText(activity, "Are you sure this is the right path?", Toast.LENGTH_SHORT).show()

                // If nothing is wrong, goes ahead with making a new file.
                else -> {
                    fileName = "$fileName.music"
                    val file = File(path,fileName)
                    FileOutputStream(file, true).bufferedWriter().use { writer ->
                        noteSheet.forEach {
                            writer.write("${it.toString()}\n")
                        }
                        this.onSave?.invoke(file.toUri())
                        FileOutputStream(file).close()

                    }
                    // After a save, compliments you and clears the current notesheet.
                    Toast.makeText(activity, "Great job, Beethoven! Your file was successful.", Toast.LENGTH_SHORT).show()
                    noteSheet.clear()
                    this.onSave?.invoke(file.toUri())


                    // Gives confirmation if the name file has been saved with the right name and path.
                    println("File saves as $fileName at $path/$fileName")
                }
            }
        }
        return view
    }
    // Function for starting a new recording.
    private fun startRecordTime() {
        if (!activeRecording){
            timeRecordingChrono.base = SystemClock.elapsedRealtime()
            timeRecordingChrono.start()
            activeRecording = true
        }
    }
    // Function for stopping the current recording.
    private fun stopRecordTime() {
        if (activeRecording) {
            timeRecordingChrono.stop()
            activeRecording = false
        }
    }
}

//private fun String.isEmpty(): Boolean { return isEmpty() }