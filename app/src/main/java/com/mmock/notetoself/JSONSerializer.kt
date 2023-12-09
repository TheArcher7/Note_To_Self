package com.mmock.notetoself

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer

class JSONSerializer (private val filename: String, private val context: Context) {

    //save notes
    @Throws(IOException::class, JSONException::class)
    fun save(notes: List<Note>) {
        //make an array in JSON format
        val jArray = JSONArray()

        //load it with notes
        for (n in notes)
            jArray.put(n.convertToJSON())

        //write it to the private disk space of app
        var writer: Writer? = null
        try{
            val out = context.openFileOutput(filename, Context.MODE_PRIVATE)
            writer = OutputStreamWriter(out)
            writer.write(jArray.toString())
        } finally {
            if (writer != null) {
                writer.close()
            }
        }
    }

    //load notes
    @Throws(IOException::class, JSONException::class)
    fun load(): ArrayList<Note>{
        val noteList = ArrayList<Note>()
        var reader: BufferedReader? = null

        try {
            val `in` = context.openFileInput(filename)
            reader = BufferedReader(InputStreamReader(`in`))
            val jsonString = StringBuilder()

            for (line in reader.readLine()) {
                jsonString.append(line)
            }

            val jArray = JSONTokener(jsonString.toString()).nextValue() as JSONArray

            for (i in 0 until jArray.length()) {
                noteList.add(Note(jArray.getJSONObject(i)))
            }
        } catch (e: FileNotFoundException) {
            //This happens when we start fresh
            //TODO add a log here
        } finally {
            reader!!.close()
        }

        return noteList
    }
}