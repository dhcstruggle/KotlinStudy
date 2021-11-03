package com.android.kotlinstudy

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.util.Xml
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream


class AppUsageActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)
        if (checkUsagePerm())
        {
            getUsageData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(mTab, "onActivityResult - requestCode:$requestCode, resultCode:$resultCode")
        when (requestCode) {
            1 -> {
                if (checkUsagePerm()) {
                    getUsageData()
                }
            }
            else ->{}
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkUsagePerm() :Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            if (AppOpsManager.MODE_ALLOWED ==
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), this.packageName)) {
                Log.d(mTab,"USAGE_STATS get success")
                return true
            } else {
                Log.d(mTab,"USAGE_STATS check fail,please allow")
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).let {
                    if (it.resolveActivity(packageManager) != null) {
                        startActivityForResult(it, 1)
                    }
                }
            }
        }

        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getUsageStatsData(statsManager: UsageStatsManager) : List<UsageStats> {
        val endT = System.currentTimeMillis()
        val statT = 0L
        val statsDaily = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, statT, endT)
        val statsWeekly = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, statT, endT)
        val statsMonthly = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, statT, endT)
        val statsYearly = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, statT, endT)

        return (statsDaily + statsWeekly + statsMonthly + statsYearly).distinct()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getEventsData(statsManager: UsageStatsManager) : UsageEvents {
        val endT = System.currentTimeMillis()
        val statT = 0L
        return statsManager.queryEvents(statT, endT)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getConfigurationsData(statsManager: UsageStatsManager) : List<ConfigurationStats> {
        val endT = System.currentTimeMillis()
        val statT = 0L
        val configsDaily = statsManager.queryConfigurations(UsageStatsManager.INTERVAL_DAILY, statT, endT)
        val configsWeekly = statsManager.queryConfigurations(UsageStatsManager.INTERVAL_WEEKLY, statT, endT)
        val configsMonthly = statsManager.queryConfigurations(UsageStatsManager.INTERVAL_MONTHLY, statT, endT)
        val configsYearly = statsManager.queryConfigurations(UsageStatsManager.INTERVAL_YEARLY, statT, endT)

        return (configsDaily + configsWeekly + configsMonthly + configsYearly).distinct()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getEventStatsData(statsManager: UsageStatsManager) : List<EventStats> {
        val endT = System.currentTimeMillis()
        val statT = 0L
        val eventStatsDaily = statsManager.queryEventStats(UsageStatsManager.INTERVAL_DAILY, statT, endT)
        val eventStatsWeekly = statsManager.queryEventStats(UsageStatsManager.INTERVAL_WEEKLY, statT, endT)
        val eventStatsMonthly = statsManager.queryEventStats(UsageStatsManager.INTERVAL_MONTHLY, statT, endT)
        val eventStatsYearly = statsManager.queryEventStats(UsageStatsManager.INTERVAL_YEARLY, statT, endT)

        return (eventStatsDaily + eventStatsWeekly + eventStatsMonthly + eventStatsYearly).distinct()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun saveAllDataToXML(usageStatsData: List<UsageStats>, eventsData:UsageEvents,
                                 configurationsData:List<ConfigurationStats>, eventStatsData:List<EventStats> ?) {
        val serializer = Xml.newSerializer()
        val os = openFileOutput("usagestats.xml", Context.MODE_PRIVATE)
        serializer.setOutput(os, "utf-8")
        serializer.startDocument("utf-8", true)
        serializer.text("\n")
        serializer.startTag(null, "usagestats")
        serializer.attribute(null, "version", "1")
        serializer.attribute(null, "endTime", System.currentTimeMillis().toString())
        serializer.text("\n")
        serializer.text("    ")
        serializer.startTag(null, "packages")
        serializer.text("\n")
        for (usageStat in usageStatsData) {
            serializer.text("        ")
            serializer.startTag(null, "package")
            serializer.attribute(null, "lastTimeActive", usageStat.lastTimeUsed.toString())
            serializer.attribute(null, "package", usageStat.packageName)
            serializer.attribute(null, "timeActive", usageStat.totalTimeInForeground.toString())
            serializer.attribute(null, "lastEvent", "0")
            serializer.endTag(null, "package")
            serializer.text("\n")
        }
        serializer.text("    ")
        serializer.endTag(null, "packages")
        serializer.text("\n")
        serializer.text("    ")
        serializer.startTag(null, "configurations")
        serializer.text("\n")
        for (config in configurationsData) {
            serializer.text("        ")
            serializer.startTag(null, "config")
            serializer.attribute(null, "lastTimeActive", config.lastTimeActive.toString())
            serializer.attribute(null, "timeActive", config.totalTimeActive.toString())
            serializer.attribute(null, "count", config.activationCount.toString())
            serializer.attribute(null, "fs", config.firstTimeStamp.toString())
            //serializer.attribute(null, "active", config.configuration.isNightModeActive.toString())
            serializer.endTag(null, "config")
            serializer.text("\n")
        }
        serializer.text("    ")
        serializer.endTag(null, "configurations")
        serializer.text("\n")
        serializer.text("    ")
        serializer.startTag(null, "event-log")
        serializer.text("\n")
        while (eventsData.hasNextEvent()) {
            val event  = UsageEvents.Event()
            eventsData.getNextEvent(event)
            serializer.text("        ")
            serializer.startTag(null, "event")
            serializer.attribute(null, "time", event.timeStamp.toString())
            serializer.attribute(null, "package", event.packageName)
            event.className?.let {
                serializer.attribute(null, "class", it)
            }
            serializer.attribute(null, "type", event.eventType.toString())
            serializer.endTag(null, "event")
            serializer.text("\n")
        }
        serializer.text("    ")
        serializer.endTag(null, "event-log")

        serializer.text("\n")
        serializer.endTag(null, "usagestats")
        serializer.flush()
        serializer.endDocument()

        File( "$filesDir/usagestats.xml").readLines().forEach { println(it) }
        copy2ExternStorage()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getUsageData() :Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val usageStatsData = getUsageStatsData(statsManager)
            val eventsData = getEventsData(statsManager)
            val configurationsData = getConfigurationsData(statsManager)
            val eventStatsData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getEventStatsData(statsManager) } else { null }

            saveAllDataToXML(usageStatsData, eventsData, configurationsData, eventStatsData)
            true
        } else {
            false
        }
    }

    private fun copy2ExternStorage() {
        val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath
        val externalXmlPath = "$externalStoragePath/usagestats.xml"
        openFileInput("usagestats.xml").use {
             FileOutputStream(externalXmlPath).use { os->
                 os.write(it.readBytes())
                 os.flush()
             }
        }
    }
}
