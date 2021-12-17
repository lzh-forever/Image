package com.example.image.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log

import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max


class DateView constructor(context: Context, attrs: AttributeSet? =null) : AppCompatTextView(context, attrs) {

    private val MONTHS = listOf("Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec")
    private val monthPaint: Paint = Paint()
    private val dayPaint: Paint = Paint()
    private val backgroundPaint: Paint = Paint()
    private val monthRect = Rect()

    var space = 10
    var date:String?=null
    var color:Color? = null


    override fun onDraw(canvas: Canvas) {
        val r = max(width, height) / 2.0f
        val cx = width/2.0f
        val cy = width/2.0f
        canvas.drawCircle(cx,cy, r, backgroundPaint)
        date?.let { date ->
            val dateArray = date.split("-")
            val month = getMonth(dateArray[1])
            val day = dateArray[2]
            monthPaint.getTextBounds(month,0,month.length,monthRect)
            val monthWidth =monthRect.width()
            val monthHeight = monthRect.height()
            canvas.drawText(month,cx,cy-space,monthPaint)
            canvas.drawText(day,cx,cy+monthHeight+space,dayPaint)

        }

    }


    init {

        monthPaint.textSize = textSize
        monthPaint.color = Color.WHITE
        monthPaint.textAlign = Paint.Align.CENTER

        dayPaint.textSize = textSize
        dayPaint.color = Color.WHITE
        dayPaint.textAlign = Paint.Align.CENTER

        backgroundPaint.color = Color.rgb(0,176,255)


    }

    private fun getMonth(month:String):String{
        val index = month.toInt()-1
        return MONTHS[index]
    }
}


