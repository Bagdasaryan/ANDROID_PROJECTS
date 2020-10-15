package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.example.reminder.ROOM_NOTIFICATIONS_DB.NotificationDatabase
import com.example.reminder.ROOM_NOTIFICATIONS_DB.Notifications
import java.util.*
import kotlin.properties.Delegates

class CreateNewNotificationActivity : AppCompatActivity() {
    // Activity elements
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnCalendar: Button
    private lateinit var cbWithoutSound: CheckBox
    private lateinit var btnCreate: Button

    //INTENT EXTRA FROM MAIN
    private lateinit var getExtraTitle: String
    private lateinit var getExtraDescription: String
    private lateinit var getExtraType: String
    private var getExtraCbWithoutSound: Boolean = false
    private var getExtraDay: Int = 0
    private var getExtraMonth: Int = 0
    private var getExtraYear: Int = 0
    private var getExtraHour: Int = 0
    private var getExtraMinute: Int = 0
    private var getExtraId: Int = 0

    // DATA AND TIME
    private var userCheckDay: Int = 0
    private var userCheckMonth: Int = 0
    private var userCheckYear: Int = 0
    private var userCheckHour: Int = 0
    private var userCheckMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_notification)

        // Intent extra
        getExtraId = intent.getIntExtra("id", -1)
        getExtraTitle = intent.getStringExtra("title").toString()
        getExtraDescription = intent.getStringExtra("description").toString()
        getExtraDay = intent.getIntExtra("day", -1)
        getExtraMonth = intent.getIntExtra("month", -1)
        getExtraYear = intent.getIntExtra("year", -1)
        getExtraHour = intent.getIntExtra("hour", -1)
        getExtraMinute = intent.getIntExtra("minute", -1)
        getExtraCbWithoutSound = intent.getBooleanExtra("cbWithoutSound", false)
        getExtraType = intent.getStringExtra("type").toString()

        // Activity elements
        etTitle = findViewById(R.id.idEtTitle)
        etDescription = findViewById(R.id.idEtDescription)
        btnCalendar = findViewById(R.id.idBtnCalendar)
        cbWithoutSound = findViewById(R.id.idCbWithoutSound)
        btnCreate = findViewById(R.id.idBtnCreate)

        // Create DataBase
        val db: NotificationDatabase = Room.databaseBuilder(applicationContext, NotificationDatabase::class.java, "notification").build()

        // Create button activation
        editTextChangeListener(etTitle, etDescription)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Passing values from intent extra
        passingValuesFromExtra(getExtraTitle, getExtraDescription,
            getExtraDay, getExtraMonth, getExtraYear, getExtraHour, getExtraMinute, getExtraCbWithoutSound)

        // CALENDAR BUTTON CLICK
        btnCalendar.setOnClickListener {
            dialogDate()
            toast("$userCheckDay.$userCheckMonth.$userCheckYear $userCheckHour:$userCheckMinute")
        }

        // CREATE BUTTON CLICK
        btnCreate.setOnClickListener {
            Thread {
                createNewNotification(db, etTitle.text.toString(), etDescription.text.toString(), cbWithoutSound.isChecked,
                    userCheckDay, userCheckMonth, userCheckYear, userCheckHour, userCheckMinute)
                Log.e("DATABASE: ", db.notificationsDao().getAll().toString())
            }.start()

            // Go to MainActivity
            intentBackToMain()
        }
    }

    /*
    * Update created notification
    * */
    private fun updateNotification(db: NotificationDatabase) {
        db.notificationsDao().update(Notifications(getExtraId, etTitle.text.toString(), etDescription.text.toString(),
            cbWithoutSound.isChecked, userCheckDay, userCheckMonth, userCheckYear, userCheckHour, userCheckMinute, false))
    }

    /*Creating new notification and adding in DB*/
    private fun createNewNotification(db: NotificationDatabase, etTitle: String, etDescription: String, cbWithoutSoundBool: Boolean,
                                      calendarDay: Int, calendarMonth: Int, calendarYear: Int, calendarHour: Int, calendarMinute: Int) {
        var id = 0
        id = if(db.notificationsDao().getAll().isEmpty()) {
            1
        } else {
            db.notificationsDao().getAll()[db.notificationsDao().getAll().size - 1].id + 1
        }
        db.notificationsDao().insert(Notifications(id, etTitle, etDescription, cbWithoutSoundBool, calendarDay, calendarMonth,
            calendarYear, calendarHour, calendarMinute, false))
    }

    /*
    * Change Button activation< when typing text in EditText
    * */
    private fun editTextChangeListener(vararg editText: EditText) {
        for(e in editText) {
            e.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    buttonActivation(btnCreate)
                }

            })
        }
    }

    fun buttonActivation(btn: Button) {
        btn.isEnabled = isFieldsFiled()
        if(isFieldsFiled()) {
            btn.setBackgroundColor(Color.parseColor("#2B90FF"))
        } else {
            btn.setBackgroundColor(Color.parseColor("#7abaff"))
        }
    }

    fun isFieldsFiled(): Boolean {
        return etTitle.text.toString().isNotEmpty() && etDescription.text.toString().isNotEmpty()
    }

    /*
    * This function displays a dialog box for selecting a date (Day, Month, Year).
    * After picking the date, the timeDialog function is called
    * */
    fun dialogDate() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            timeDialog(dayOfMonth, monthOfYear, year)
        }, userCheckYear, userCheckMonth, userCheckDay).show()
    }

    /*
    * This function calls the time selection dialog box (Hours, Minutes).
    * When choosing a time, the fields that are declared at the beginning of
    *  the class as the time that the user has selected (userCheckDay, userCheckMonth, userCheckYear, userCheckHour, userCheckMinute)
    *  are passed the selected values.
    * Parameters:
    *   day - the day that was selected in the dialogDate() function
    *   month - the month that was selected in the dialogDate () function
    *   year - the year that was selected in the dialogDate () function
    * */
    private fun timeDialog(day: Int, month: Int, year: Int) {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            userCheckDay = day
            userCheckMonth = month
            userCheckYear = year
            userCheckHour = hour
            userCheckMinute = minute
        }, userCheckHour, userCheckMinute, true).show()
    }

    /*
    * This function, when going to CreateNewNotificationActivity,
    * passes the following values to the text fields, checkbox, and date:
    *    IF THE USER PASSED ON RECYCLERVIEW,
    *    DATA WILL BE TRANSFERRED THAT WAS AT THE DATA TO RECYCLERVIEW, AND IF THE USER GOES TO PRESS DATE - PRESENT
    * */
    private fun passingValuesFromExtra(title: String, description: String, day: Int, month: Int, year: Int, hour: Int, minute: Int,
                                       cbWithoutSoundBool: Boolean) {
        etTitle.setText(title)
        etDescription.setText(description)
        // TIME
        userCheckDay = day
        userCheckMonth = month
        userCheckYear = year
        userCheckHour = hour
        userCheckMinute = minute
        //CheckBox
        cbWithoutSound.isChecked = cbWithoutSoundBool
    }

    private fun alert() {
        val db: NotificationDatabase = Room.databaseBuilder(applicationContext, NotificationDatabase::class.java, "notification").build()
        val intent: Intent = Intent(this, MainActivity::class.java)

        AlertDialog.Builder(this)
            .setTitle("Изменение")
            .setMessage("Хотите сохранить изменения?")
            .setPositiveButton("Да") {
                dialog, which -> run {
                    if(getExtraType == "editing") {
                        Thread {
                            updateNotification(db)
                        }.start()
                    } else {
                        Thread {
                            createNewNotification(db, etTitle.text.toString(), etDescription.text.toString(), cbWithoutSound.isChecked,
                                userCheckDay, userCheckMonth, userCheckYear, userCheckHour, userCheckMinute)
                        }.start()
                    }
                }
                intentBackToMain()
            }
            .setNegativeButton("Нет") {
                dialog, which -> run {
                    dialog.cancel()
                }
                intentBackToMain()
            }
            .show()
    }

    private fun isBeenChanges(): Boolean {
        return  etTitle.text.toString() == getExtraTitle && etDescription.text.toString() == getExtraDescription &&
                userCheckYear == getExtraYear && userCheckMonth == getExtraMonth && userCheckDay == getExtraDay &&
                userCheckHour == getExtraHour && userCheckMinute == getExtraMinute && cbWithoutSound.isChecked == getExtraCbWithoutSound
    }

    private fun intentBackToMain() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /*
    * Save the changes or new notification in DB from menu
    * */
    private fun saveFromMenu() {
        val db: NotificationDatabase = Room.databaseBuilder(applicationContext, NotificationDatabase::class.java, "notification").build()

        if(getExtraType == "editing") {
            Thread {
                updateNotification(db)
            }.start()
        } else {
            Thread {
                createNewNotification(db, etTitle.text.toString(), etDescription.text.toString(), cbWithoutSound.isChecked,
                    userCheckDay, userCheckMonth, userCheckYear, userCheckHour, userCheckMinute)
                Log.e("DATABASE: ", db.notificationsDao().getAll().toString())
            }.start()
        }
        intentBackToMain()
    }

    /*
    * Delete notification in DB from menu
    * */
    private fun deleteFromMenu() {
        val db: NotificationDatabase = Room.databaseBuilder(applicationContext, NotificationDatabase::class.java, "notification").build()

        AlertDialog.Builder(this)
            .setTitle("Удалить")
            .setMessage("Вы действительно хотите удалить данное уведомление?")
            .setPositiveButton("Да") {
                _, _ -> run {
                    Thread {
                        db.notificationsDao().delete(Notifications(getExtraId, getExtraTitle, getExtraDescription, getExtraCbWithoutSound,
                            getExtraDay, getExtraMonth, getExtraYear, getExtraHour, getExtraMinute, false))
                    }.start()
                }
                intentBackToMain()
            }
            .setNegativeButton("Нет") {
                dialog, _ -> run {
                    dialog.cancel()
                }
                intentBackToMain()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        if(isBeenChanges() || !isFieldsFiled()) {
            onBackPressed()
        } else {
            alert()
        }

        return true
    }

    private fun Context.toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
    }

    // MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuSave -> {
                saveFromMenu()
            }
            R.id.menuDelete -> {
                deleteFromMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
