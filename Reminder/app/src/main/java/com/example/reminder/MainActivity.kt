@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.example.reminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.reminder.RECYCLER_NOTIFICATIONS.AdapterRecyclerViewNotes
import com.example.reminder.RECYCLER_NOTIFICATIONS.ModelRecyclerViewNotifications
import com.example.reminder.ROOM_NOTIFICATIONS_DB.NotificationDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // START SERVICE WORK
        startService(Intent(this, NotificationService::class.java))

        // Create DataBase
        val db: NotificationDatabase = Room.databaseBuilder(applicationContext, NotificationDatabase::class.java, "notification").build()
        Thread {
            Log.e("DB: ", db.notificationsDao().getAll().toString())
        }.start()

//        DELETE ALL
//        Thread {
//            db.notificationsDao().deleteAll(db.notificationsDao().getAll())
//        }.start()

        //For test
        val items: ArrayList<ModelRecyclerViewNotifications> = ArrayList()
        Thread {
            for(i in db.notificationsDao().getAll()) {
                items.add(ModelRecyclerViewNotifications(i.id, i.title.toString(), i.description.toString(), i.cbWithoutSound,
                    i.day, i.month, i.year, i.hour, i.minute))
            }
        }.start()

        Log.e("SORTING: ", items.toString())
        // Output notifications on display
        recyclerViewOutput(items)
        outputSearchResult(items)

        // Add new notification
        floatingActionButtonClick()
    }

    private fun floatingActionButtonClick() {
        findViewById<FloatingActionButton>(R.id.idFabAdd).setOnClickListener {
            val c = Calendar.getInstance()
            val d = c.get(Calendar.DAY_OF_MONTH)
            val mon = c.get(Calendar.MONTH)
            val y = c.get(Calendar.YEAR)
            val h = c.get(Calendar.HOUR_OF_DAY)
            val min = c.get(Calendar.MINUTE)
            openIntent(-1, "", "",  d, mon, y, h, min, false, "addNew")
        }
    }

    fun outputSearchResult(list: ArrayList<ModelRecyclerViewNotifications>) {
        val notificationSearch: androidx.appcompat.widget.SearchView = findViewById(R.id.notification_search)
        notificationSearch.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerViewOutput( findSearchNotification( list, newText.toString() ) )
                return false
            }

        })
    }

    fun findSearchNotification(list: ArrayList<ModelRecyclerViewNotifications>, text: String): ArrayList<ModelRecyclerViewNotifications> {
        val arr: ArrayList<ModelRecyclerViewNotifications> = ArrayList()
        if(text.isNotEmpty()) {
            for(i in list) {
                if(i.title.contains(text, ignoreCase = true) || i.description.contains(text, ignoreCase = true) ||
                        i.day.toString().contains(text, ignoreCase = true) || i.month.toString().contains(text, ignoreCase = true) ||
                        i.year.toString().contains(text, ignoreCase = true) || i.hour.toString().contains(text, ignoreCase = true)  ||
                        i.minute.toString().contains(text, ignoreCase = true)) {
                    arr.add(ModelRecyclerViewNotifications(i.id, i.title, i.description, i.cbWithoutSound, i.day, i.month, i.year, i.hour, i.minute))
                }
            }
            return arr
        }
        return list
    }

    /*
    * At first we are outputing the relevant notifications, then expired
    * */
    private fun firstRelevantThenExpired(list: ArrayList<ModelRecyclerViewNotifications>) {
        var count = 0
        val listRelevant: ArrayList<ModelRecyclerViewNotifications> = ArrayList()
        for(i in 0..(list.size-1)) {
            if(isDateExpired(list[i].day, list[i].month, list[i].year, list[i].hour, list[i].minute)) {
                count++
                list.add(ModelRecyclerViewNotifications(list[i].id, list[i].title, list[i].description, list[i].cbWithoutSound,
                    list[i].day, list[i].month, list[i].year, list[i].hour, list[i].minute))
            }
        }
        for(i in 0..(count-1)) {
            list.removeAt(0)
        }
    }

    /*
    * Sorting RecyclerView by Date
    * */
    @SuppressLint("SimpleDateFormat")
    fun sortingOfDate(items: ArrayList<ModelRecyclerViewNotifications>) {
        val dateFormat = "yyyy-MM-dd HH:mm"
        val parser = SimpleDateFormat(dateFormat)
        items.sortWith(compareBy{
            SimpleDateFormat(dateFormat).format(parser.parse("${it.year}-${it.month}-${it.day} ${it.hour}:${it.minute}"))
        })
    }

    /*
    * Creating recyclerView and output it
    * */
    private fun recyclerViewOutput(items: ArrayList<ModelRecyclerViewNotifications>) {
        Thread.sleep(100)
        sortingOfDate(items)
        firstRelevantThenExpired(items)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Click on RecyclerView
        val adapter = AdapterRecyclerViewNotes(items,
            object: AdapterRecyclerViewNotes.Callback {
            override fun onItemClicked(item: ModelRecyclerViewNotifications) {

                openIntent(item.id, item.title, item.description, item.day, item.month, item.year, item.hour, item.minute,
                    item.cbWithoutSound, "editing")
            }
        })
        recyclerView.adapter = adapter
    }

    /*
    * This function redirects to CreateNewNotificationActivity.
    * Parameters:
    *   id - it is the identifier of the item in the Database.
    *           If the user enters by clicking on the RecyclerView,
    *           then the id is passed the corresponding value from the database, otherwise -1 is passed
    *
    *   title - this is the title. If the user enters by clicking on the RecyclerView,
    *               then the value that he has is passed, otherwise an empty string is passed
    *
    *   description - this is the description. If the user enters by clicking on the RecyclerView,
    *                   then the value that he has is passed, otherwise an empty string is passed
    *
    *   time - this is the time. If the user enters by clicking on the RecyclerView,
    *           then the value that he has is passed, otherwise an empty string is passed
    *
    *   cbWithoutSound - takes two meanings: true, false. Indicates whether the button is pressed or not
    *
    *   type - this is the type of transition. The user can navigate both by clicking on the RecyclerView,
    *            and by clicking on the button to create a new notification.
    *            There are two types - editing, addNew.
    *
    *           *editing - indicates that the user wants to edit an already existing notification
    *           *addNew - indicates that the user wants to create a new notification
    * */
    fun openIntent(id: Int, title: String, description: String, day: Int, month: Int, year: Int, hour: Int, minute: Int,
                   cbWithoutSound: Boolean, type: String) {
        startActivity(Intent(this, CreateNewNotificationActivity::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("description", description)
            putExtra("day", day)
            putExtra("month", month)
            putExtra("year", year)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("cbWithoutSound", cbWithoutSound)
            putExtra("type", type)
        })
    }
}

// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

// PARAMETERS: day, month, year, hour, minute
@SuppressLint("SimpleDateFormat")
public fun isDateExpired(vararg timeInDB: Int): Boolean {
    val c = Calendar.getInstance()

    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm")

    val dateInNotification = SimpleDateFormat("yyyy-MM-dd HH:mm").
    format(parser.parse("${timeInDB[2]}-${timeInDB[1]}-${timeInDB[0]} ${timeInDB[3]}:${timeInDB[4]}"))
    val currentlyDate = SimpleDateFormat("yyyy-MM-dd HH:mm").
    format(parser.parse("${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH)}-${c.get(Calendar.DAY_OF_MONTH)}" +
            " ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"))

    if(currentlyDate >= dateInNotification) {
        return true
    }
    return false
}
























