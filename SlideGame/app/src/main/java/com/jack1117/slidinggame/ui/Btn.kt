package com.jack1117.slidinggame.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import com.jack1117.slidinggame.R

//btn set
//長寬設定, 位置設定
class Btn(res: Resources, tag: Char, size: Int, x: Float, y: Float) {
    //設定圖片
    private val unpressedimage: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            res,
            R.drawable.unpressed_button
        ), size, size, true
    )
    private val pressedimage: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(
            res,
            R.drawable.pressed_button
        ), size, size, true
    )
    private val bounds: RectF = RectF(x, y, x + size, y + size)

    private var btn_pressed = false
    private var btn_tag: Char = tag

    fun _DrawBtn(c: Canvas){
        if(btn_pressed){
            c.drawBitmap(pressedimage, bounds.left, bounds.top, null)
        } else {
            c.drawBitmap(unpressedimage, bounds.left, bounds.top, null)
        }
    }

    fun _Contains(x: Float, y: Float) : Boolean{
        return bounds.contains(x, y)
    }

    fun _BtnPressed(){
        btn_pressed = true
    }
    fun _BtnUnpressed(){
        btn_pressed = false
    }

    fun getX() : Float{
        return bounds.left
    }
    fun getY() : Float{
        return bounds.top
    }

    fun getBtnTag() : Char{
        return btn_tag
    }

    fun _InColumn() : Boolean{
        return btn_tag == '1' || btn_tag == '2' || btn_tag == '3' || btn_tag == '4' || btn_tag == '5'
    }
    fun _InRow() : Boolean{
        return btn_tag == 'A' || btn_tag == 'B' || btn_tag == 'C' || btn_tag == 'D' || btn_tag == 'E'
    }
}