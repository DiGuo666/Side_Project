package com.jack1117.slidinggame.ui

import android.os.Handler
import android.os.Looper
import android.os.Message

//handler
class Timer(looper: Looper) : Handler(looper){
    private val listeners: ArrayList<TickListener> = ArrayList()
    private val delay: Long = 10

    private var paused = false

    init {
        sendMessageDelayed(Message.obtain(), 0)
    }

    override fun handleMessage(msg: Message) {
        if(!paused){
            _NotifyListeners()
        }
        sendMessageDelayed(Message.obtain(), delay)
    }

    fun _ClearAll(){
        listeners.clear()
    }

    fun _Register(t: TickListener){
        listeners.add(t)
    }
    fun _UnRegister(t: TickListener){
        listeners.remove(t)
    }

    fun setPause(){
        paused = true
    }
    fun setUnPause(){
        paused = false
    }

    fun _NotifyListeners(){
        for(listener in listeners){
            listener.Tick()
        }
    }
}
