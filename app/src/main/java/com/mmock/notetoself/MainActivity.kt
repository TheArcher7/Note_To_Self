package com.mmock.notetoself

// This is the code covered in Ch16 expanding on code covered in ch14

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmock.notetoself.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mSerializer: JSONSerializer? = null
    private var noteList: ArrayList<Note>? = null
    //private val noteList = ArrayList<Note>()
    private var recyclerView: RecyclerView? = null
    private var adapter: NoteAdapter? = null

    //preferences
    private var showDividers: Boolean = false

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            val dialog = DialogNewNote()
            dialog.show(supportFragmentManager, "")
        }

        mSerializer = JSONSerializer("NoteToSelf.json", applicationContext)

        try{
            noteList = mSerializer!!.load()
        } catch (e: Exception) {
            noteList = ArrayList()
            Log.e("Error loading notes: ", "", e)
        }

        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        adapter = NoteAdapter(this, noteList!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        //set the adapter
        recyclerView!!.adapter = adapter
    }

    fun createNewNote(n: Note){
        //temporary code
        //tempNote = n
        noteList!!.add(n)
        adapter!!.notifyDataSetChanged()
    }

    fun showNote(noteToShow: Int) {
        val dialog = DialogShowNote()
        dialog.sendNoteSelected(noteList!![noteToShow])
        dialog.show(supportFragmentManager, "")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {

                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("Note to self", Context.MODE_PRIVATE)

        showDividers = prefs.getBoolean("dividers", true)

        //add a neat dividing line between list items
        if (showDividers)
            recyclerView!!.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        else {
            //check there are some dividers or the app will crash
            if(recyclerView!!.itemDecorationCount > 0)
                recyclerView!!.removeItemDecorationAt(0)
        }
    }

    override fun onPause() {
        super.onPause()

        saveNotes()
    }

    private fun saveNotes() {
        try {
            mSerializer!!.save(this.noteList!!)
        } catch (e: Exception) {
            Log.e("Error saving notes", "", e)
        }
    }

}