package com.jack1117.slidinggame.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import com.jack1117.slidinggame.R

class Token(res: Resources, size: Int, x: Float, y:Float,var row: Char,var column: Char) :
    TickListener {
    companion object{
        var player = 0 //偶數為玩家一，奇數為玩家二
        var movers = 0

        fun _TokenIsMoving() : Boolean{
            return movers > 0
        }
    }

    private val bounds = RectF(x, y, x + size, y + size)
    private val dog: Bitmap
    private val velocity = PointF(0f, 0f)  //速度
    private val destination = PointF(x, y)

    private var falling = false

    init {
        dog = if(player % 2 == 0){
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.doggy3), size, size, true)
        } else{
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.doggy4), size, size, true)
        }
        player++
    }

    fun _DrawToken(c: Canvas){
        c.drawBitmap(dog, bounds.left, bounds.top, null)
    }

    fun _ChangeVelocity(x: Float, y: Float){
        if(x != 0f || y != 0f){
            movers++
        } else{
            movers--
        }
        velocity.x = x
        velocity.y = y
    }

    fun setDestination(x: Float, y: Float){
        destination.x += x
        destination.y += y
    }

    private fun _MoveToken(){
        if(falling){
            velocity.y *= 2f
        } else if(velocity.x != 0f && destination.x - bounds.left <= 0){
            //停止token移動
            _ChangeVelocity(0f, 0f)
            //超出框線的token
            if (column > '5') {
                _ChangeVelocity(0f, 1f)
                falling = true
            }
        } else if (velocity.y != 0f && destination.y - bounds.top <= 0){
            _ChangeVelocity(0f, 0f)
            if (row > 'E') {
                _ChangeVelocity(0f, 1f)
                falling = true
            }
        }
        bounds.left += velocity.x
        bounds.top += velocity.y
    }

    fun _TokenIsVisible(h: Float) : Boolean{
        return bounds.top <= h
    }

    override fun Tick() {
        _MoveToken()
    }
}