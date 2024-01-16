package com.jack1117.slidinggame.rule

import android.content.res.Resources
import com.jack1117.slidinggame.R
import kotlin.random.Random

class GameBoard(val res: Resources) {
    //Grid => 5x5 two dimensional array
    private val grid = arrayOf(
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK)
    )
    private val board = 5

    private var currentPlayer: Player = Player.X

    fun _AIMove() : Int{
        //check all rows
        for(i in 0 until board){
            for(j in 0 .. 2){
                val firstelement = grid[i][j]
                var threeInARow = true
                for(k in j .. j + 2){
                    if(grid[i][k] != firstelement){
                        threeInARow = false
                        break
                    }
                }
                if(threeInARow && firstelement == Player.X){
                    return i + 5
                }
            }
        }
        //check all columns
        for(i in 0 until board){
            for(j in 0 .. 2){
                val firstelement = grid[j][i]
                var threeInARow = true
                for(k in j .. j + 2){
                    if(grid[k][i] != firstelement){
                        threeInARow = false
                        break
                    }
                }
                if(threeInARow && firstelement == Player.X){
                    return i
                }
            }
        }

        return Random.nextInt(0,10)
    }

    fun _SubmitMove(move: Char){
        if(move in '1'..'5'){
            val column = move.toString().toInt() - 1
            val neighbors: ArrayList<Player> = ArrayList()
            //確認是否為空白
            println(column)
            for(row in 0 until board){
                if(grid[row][column] != Player.BLANK){
                    neighbors.add(grid[row][column])
                } else{
                    break
                }
            }
            //往下移
            for(index in neighbors.indices){
                if(index + 1 < board) {
                    grid[index + 1][column] = neighbors[index]
                }
            }
            grid[0][column] = currentPlayer
        } else {
            //UTF-16 code
            val row = (move.code - 'A'.code) //0 = 'A', 1 = 'B' ...
            val neighbors: ArrayList<Player> = ArrayList()
            println(row)
            for(column in 0 until board){
                if(grid[row][column] != Player.BLANK){
                    neighbors.add(grid[row][column])
                } else{
                    break
                }
            }
            //往下移
            for(index in neighbors.indices){
                if(index + 1 < board) {
                    grid[row][index + 1] = neighbors[index]
                }
            }
            grid[row][0] = currentPlayer
        }

        currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X

        //print grid
//        for(arr in grid){
//            println("")
//            for(element in arr){
//                print("$element, ")
//            }
//            println("")
//        }
//        println(res.getString(R.string.Separation_Line))
    }

    fun _CheckWinner() : Player{
        val winners: ArrayList<Player> = ArrayList()
        //check all rows
        for(i in 0 until board){
            if(grid[i][0] != Player.BLANK){
                val firstelement = grid[i][0]
                var connect = true
                for(j in 0 until board){
                    if(grid[i][j] != firstelement){
                        connect = false
                        break
                    }
                }
                //避免贏家重複
                if(connect && !winners.contains(firstelement)){
                    winners.add(firstelement)
                }
            } else {
                break
            }
        }

        //check all columns
        for(i in 0 until board){
            if(grid[0][i] != Player.BLANK){
                val firstelement = grid[0][i]
                var connect = true
                for(j in 0 until board){
                    if(grid[j][i] != firstelement){
                        connect = false
                        break
                    }
                }
                if(connect && !winners.contains(firstelement)){
                    winners.add(firstelement)
                }
            } else {
                break
            }
        }

        //check diagonals
        if(grid[0][0] != Player.BLANK){
            val firstelement = grid[0][0]
            var connect = true
            for(i in 0 until board){
                if(grid[i][i] != firstelement){
                    connect = false
                    break
                }
            }
            if(connect){
                return firstelement
            }
        }
        if(grid[board - 1][0] != Player.BLANK){
            val firstelement = grid[board - 1][0]
            var connect = true
            for(i in 0 until board){
                if(grid[board - 1 - i][i] != firstelement){
                    connect = false
                    break
                }
            }
            if(connect){
                return firstelement
            }
        }

        if(winners.size == 1){
            return winners[0]
        } else if(winners.size > 1){
            return Player.TIE
        }
        return Player.BLANK
    }
}