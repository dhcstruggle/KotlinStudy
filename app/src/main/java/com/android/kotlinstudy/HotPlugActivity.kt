package com.android.kotlinstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.kotlinstudy.utils.ToastUtil
import com.android.kotlinstudy.utils.USB.getDescription
import com.android.kotlinstudy.utils.USB.readConfigurationDescriptor
import com.android.kotlinstudy.utils.USB.readDeviceDescriptor
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.fs.UsbFile
import kotlinx.android.synthetic.main.activity_hot_plug.*
import java.text.ParseException
import java.util.*


class HotPlugActivity : AppCompatActivity() {
    private val mMediaEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        printStatus(getString(R.string.status_removed))
                        printDeviceDescription(it)
                    }
                }

                else -> {

                }
            }
        }
    }

    private val mUsbManager by lazy { getSystemService(UsbManager::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_plug)

        IntentFilter().run {
//            addAction(Intent.ACTION_MEDIA_MOUNTED)
//            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
//            addAction(Intent.ACTION_MEDIA_EJECT)
//            addAction(Intent.ACTION_MEDIA_REMOVED)
//            addAction(Intent.ACTION_MEDIA_CHECKING)
//            addAction(Intent.ACTION_MEDIA_SCANNER_STARTED)
//            addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED)
//            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            //addDataScheme("file")
            registerReceiver(mMediaEventReceiver, this)
        }

        handleIntent(intent)
        ToastUtil.showMsg(this, "reg bc")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMediaEventReceiver)
        ToastUtil.showMsg(this, "unreg bc")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun printDeviceDescription(device: UsbDevice) {
        val result = device.getDescription() + "\n\n"
        printResult(result)
    }

    private fun printDeviceDetails(device: UsbDevice) {
        val connection = mUsbManager.openDevice(device)

        val deviceDescriptor = try {
            //Parse the raw device descriptor
            connection.readDeviceDescriptor()
        } catch (e: IllegalArgumentException) {

            null
        }

        val configDescriptor = try {
            //Parse the raw configuration descriptor
            connection.readConfigurationDescriptor()
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: ParseException) {
            null
        }

        val showText = "$deviceDescriptor\n\n$configDescriptor" + getStorageFileList()
        tv_result.text = showText
        connection.close()
    }

    private fun printDeviceList() {
        val connectedDevices = mUsbManager.deviceList

        if (connectedDevices.isEmpty()) {
            printResult("No Devices Currently Connected")
        } else {
            val builder = buildString {
                append("Connected Device Count: ")
                append(connectedDevices.size)
                append("\n\n")
                for (device in connectedDevices.values) {
                    //Use the last device detected (if multiple) to open
                    append(device.getDescription())
                    append("\n\n")
                }
                append(getStorageFileList())
            }

            printResult(builder)
        }
    }

    private fun handleIntent(intent: Intent) {
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device != null) {
            printStatus(getString(R.string.status_added))
            printDeviceDetails(device)
        } else {
            // List all devices connected to USB host on startup
            printStatus(getString(R.string.status_list))
            printDeviceList()
        }
    }

    private fun printStatus(status: String) {
        tv_status.text = status
    }

    private fun printResult(result: String) {
        tv_result.text = result
    }
    private fun fSize(sizeInByte: Long): String? {
        return if (sizeInByte < 1024) String.format(
            "%s",
            sizeInByte
        ) else if (sizeInByte < 1024 * 1024) java.lang.String.format(
            Locale.CANADA,
            "%.2fKB",
            sizeInByte / 1024.0
        ) else if (sizeInByte < 1024 * 1024 * 1024) java.lang.String.format(
            Locale.CANADA,
            "%.2fMB",
            sizeInByte / 1024.0 / 1024
        ) else java.lang.String.format(Locale.CANADA, "%.2fGB", sizeInByte / 1024.0 / 1024 / 1024)
    }

    private fun getStorageFileList() :String {
        //val mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        var builder :String = ""
        try {
            val storageDevices = UsbMassStorageDevice.getMassStorageDevices(this)
            for (storageDevice in storageDevices) {
                // 申请USB权限
//                if (!mUsbManager.hasPermission(storageDevice.usbDevice)) {
//                    mUsbManager.requestPermission(storageDevice.usbDevice, mPermissionIntent)
//                    break
//                }
                // 初始化
                storageDevice.init()
                // 获取分区
                val partitions = storageDevice.partitions
                if (partitions.isEmpty()) {
                    continue
                }

                for (partition in partitions) {
                    val fileSystem = partition.fileSystem
                    builder = buildString {
                        append("Volume Label: ${fileSystem.volumeLabel}\n")
                        append("Capacity: ${fSize(fileSystem.capacity)}\n")
                        append("Occupied Space: ${fSize(fileSystem.occupiedSpace)}\n")
                        append("Free Space: ${fSize(fileSystem.freeSpace)}\n")
                        append("Chunk size: ${fSize(fileSystem.chunkSize.toLong())}\n")
                        val root: UsbFile = fileSystem.rootDirectory
                        val files: Array<UsbFile> = root.listFiles()
                        for (file in files)
                            append("文件: ${file.name}\n")
                    }


//                    // 新建文件
//                    val newFile: UsbFile =
//                        root.createFile("hello_" + System.currentTimeMillis() + ".txt")
//                    logShow("新建文件: " + newFile.name)
//
//                    // 写文件
//                    // OutputStream os = new UsbFileOutputStream(newFile);
//                    val os: OutputStream =
//                        UsbFileStreamFactory.createBufferedOutputStream(newFile, fileSystem)
//                    os.write(("hi_" + System.currentTimeMillis()).toByteArray())
//                    os.close()
//                    logShow("写文件: " + newFile.name)
//
//                    // 读文件
//                    // InputStream is = new UsbFileInputStream(newFile);
//                    val `is`: InputStream =
//                        UsbFileStreamFactory.createBufferedInputStream(newFile, fileSystem)
//                    val buffer = ByteArray(fileSystem.chunkSize)
//                    var len: Int
//                    val sdFile = File("/sdcard/111")
//                    sdFile.mkdirs()
//                    val sdOut =
//                        FileOutputStream(sdFile.getAbsolutePath().toString() + "/" + newFile.name)
//                    while (`is`.read(buffer).also { len = it } != -1) {
//                        sdOut.write(buffer, 0, len)
//                    }
//                    `is`.close()
//                    sdOut.close()
//                    logShow("读文件: " + newFile.name.toString() + " ->复制到/sdcard/111/")
                }

                storageDevice.close()
            }
        } catch (e: Exception) {
            return builder
        }

        return builder
    }

}