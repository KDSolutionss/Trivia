package com.example.trivia.game

class Processor {
    var score=0.0
    private var delta=1.0
    var question=""
    var answer=""
    var tryAnswer=""
    private var counter=1
    var showable=" "
    fun getCipher():String{
        return answer
            .map {'*'}
            .toString()
            .trim('[',']')
            .replace(',',' ')

        }
    fun isAnswerRight():Boolean
    {

        val a=answer.lowercase().trim(' ') == tryAnswer.lowercase().trim(' ')
        if (a)
        {score+=delta
        counter=1
        delta= 1.0
        }
        return a
    }
    fun getHint():Pair<String,Int>
    {

        return if (counter<4) {
            counter++
            if (delta>0.25) {
                delta-=0.25
            }
            else
            {
                delta=0.01
            }
            showable=answer.map { if (answer.indexOf(it)<counter-1) it else "*" }
                .toString()
                .trim('[',']')
                .replace(',',' ')
            return Pair(answer.map { if (answer.indexOf(it)<counter-1) it else "*" }
                .toString()
                .trim('[',']')
                .replace(',',' '),1)

        } else {
            Pair("Вы использовали все доступные подсказки",0)
        }

    }
    fun getMixed():Pair<String,Int>
    {
        return if (counter==1) {
            if (delta>0.5) {
                delta-=0.5
            }
            else
            {
                delta=0.01
            }
            counter+=2
            showable=answer.toList().shuffled().joinToString(" ")
            Pair(answer.toList().shuffled().joinToString(" "),1)
        } else {
            Pair("Больше нельзя использовать подсказки",0)
        }
    }
    fun erase_counter()
    {
        counter=1
    }


}
