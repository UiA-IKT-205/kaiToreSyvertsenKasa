package com.example.pianomaster

import android.app.Activity
import android.app.TaskStackBuilder.create
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat.apply
import androidx.fragment.app.Fragment
import com.example.pianomaster.databinding.FragmentPianoBinding
import data.Note
import kotlinx.android.synthetic.main.fragment_full_tone_piano_key.view.*
import kotlinx.android.synthetic.main.fragment_half_tone_piano_key.view.*
import kotlinx.android.synthetic.main.fragment_piano.view.*
import java.io.File
import java.io.FileOutputStream
import java.net.URI.create
import java.util.Currency.getInstance

class PianoLayout : Fragment() {

    private var _binding: FragmentPianoBinding? = null
    private val binding get() = _binding!!

    //Creating a list of Full Notes
    private val fullTones = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2", "A2", "B2")
    //Creating a list of Half Notes
    private val halfTones = listOf("C#", "D#", "F#", "G#", "A#", "C#2", "D#2", "F#2", "G#2", "A#2")

    private var score:MutableList<Note> = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
                    var endPlay:Long = 0 // Added for possible later use

                    fullTonePianoKey.onKeyDown = {
                        startPlay = System.nanoTime()
                        println("Piano key down $it")

                    }

                    fullTonePianoKey.onKeyUp = {
                        val note = Note(it, startPlay, endPlay)
                        var endPlay = System.nanoTime()
                        score.add(note)
                        println("Piano key up $note")

                    }
                    fragmentTransaction.add(view.fullToneKeyLayout.id, fullTonePianoKey, "note_$it")
                }
                //Makes a key for each tone in Half Tones.
                halfTones.forEach {
                    val halfTonePianoKey = HalfTonePianoKeyFragment.newInstance(it)

                    // Variables to track time played.
                    var startPlay:Long = 0
                    var endPlay:Long = 0 // Added for possible later use

                    halfTonePianoKey.onKeyDown = {
                        println("Piano key down $it")
                    }

                    halfTonePianoKey.onKeyUp = {
                        val note = Note(it, startPlay, endPlay)
                        var endPlay = System.nanoTime()
                        score.add(note)
                        println("Piano key up $it")
                    }
                    fragmentTransaction.add(view.halfToneKeyLayout.id, halfTonePianoKey, "note_$it")
        }
        fragmentTransaction.commit()
        view.saveScoreBt.setOnClickListener {
            var fileName = view.fileNameTextEdit.text.toString()
            val path = this.activity?.getExternalFilesDir(null)
            if (score.count() > 0 && fileName.isNotEmpty() && path != null){
                fileName = "$fileName.musikk"
                FileOutputStream(File(path,fileName),true).bufferedWriter().use { writer ->
                    // bufferdWriter lever her
                    score.forEach {
                        writer.write("${it.toString()}\n")
                    }
                }
            } else {
                /// TODO: What to do?
            }
        }
        return view
    }

}
