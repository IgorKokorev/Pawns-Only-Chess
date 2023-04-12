package chess

import kotlin.math.abs

class ChessException(msg: String): Exception(msg)
const val INV_INP = "Invalid Input"

val chessBoard = ChessBoard()
var playerWName = ""
var playerBName = ""
var colorTurn = 'W'


fun main() {

    println("Pawns-Only Chess")

    println("First Player's name:")
    playerWName = readln()
    println("Second Player's name:")
    playerBName = readln()

    chessBoard.startPawnsOnly()
    printBoard()

    game()
}

fun game() {
    val correctMove = Regex("[a-h][1-8][a-h][1-8]")
    while (true) {
        if (colorTurn == 'W') print(playerWName)
        else print(playerBName)
        println("'s turn:")
        val move = readln()
        if(move.equals("exit")) break
        if (!correctMove.matches(move)) {
            println(INV_INP)
            continue
        }

        try {
            chessBoard.move(move)
        } catch (e: ChessException) {
            println(e.message)
            continue
        }
        printBoard()
        colorTurn = if (colorTurn == 'W') 'B' else 'W'
    }
    println("Bye!")
}

fun printBoard() {
    printDelimiter()
    for (i in 7 downTo 0) {
        printRow(i)
        printDelimiter()
    }
    print(" ")
    for (ch in 'a'..'h') print("   $ch")
    println("")
}

fun printDelimiter() {
    print("  +")
    repeat(8) { print("---+") }
    println("")
}

fun printRow( n: Int) {
    print("${n+1} |")
    for (i in 0..7) {
        if (chessBoard.board[n][i] != null)
            print(" ${chessBoard.board[n][i]!!.color} |")
        else print("   |")
    }
    println("")
}

class ChessBoard {
    val board: Array<Array<ChessPiece?>> =  Array(8) { Array(8) { null } }

    // fill the board with pawns only
    fun startPawnsOnly() {
        for (i in 0..7) {
            this.board[1][i] = ChessPiece('W')
            this.board[6][i] = ChessPiece('B')
        }
    }

    // check if 2-cell move has just been made
    private var isPassed = false
    private var colPassed = -1

    // move a pawn
    fun move(move: String) {

        val fromCol: Int = move[0] - 'a'
        val fromRow: Int = move[1] - '1'
        val toCol: Int = move[2] - 'a'
        val toRow: Int = move[3] - '1'

        val piece = chessBoard.board[fromRow][fromCol]
        // check if we try to make a move from empty cell
        // or to move a piece of another color
        if (piece == null || piece.color != colorTurn)
            throw ChessException(
            "No ${if(colorTurn == 'W') "white" else "black"} " +
                    "pawn at " + move.substring(0..1)
            )

        val direction = if (colorTurn == 'W') 1 else -1

        // check if we change column
        if (fromCol != toCol) checkTake(direction, fromRow, fromCol, toRow, toCol)
        else checkMove(direction, fromRow, fromCol, toRow, toCol)

    }

    private fun checkTake(direction: Int, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        // check if we move too far
        if (abs(fromCol - toCol) > 1)
            throw ChessException(INV_INP)
        if ((toRow - fromRow) * direction != 1)
            throw ChessException(INV_INP)


        // check "en passant"
        if (chessBoard.board[toRow][toCol] == null) {
            if (!isPassed || toCol != colPassed || (fromRow * 2 - 7) != direction)
                throw ChessException(INV_INP)
        } else if (chessBoard.board[toRow][toCol]!!.color == colorTurn)
            throw ChessException(INV_INP)

        // Moving
        chessBoard.board[toRow][toCol] = chessBoard.board[fromRow][fromCol]
        chessBoard.board[fromRow][fromCol] = null
        isPassed = false
    }

    private fun checkMove(direction: Int, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        // check if the target cell is empty
        if (chessBoard.board[toRow][toCol] != null)
            throw ChessException(INV_INP)
        // move back or in place
        if ((toRow - fromRow) * direction <= 0)
            throw ChessException(INV_INP)
        // more than 2 cells forward
        if ((toRow - fromRow) * direction > 2)
            throw ChessException(INV_INP)
        // 2 cells forward but not from the beginning
        if (((toRow - fromRow) * direction == 2) and
            ((7 - fromRow * 2) != direction * 5))
            throw ChessException(INV_INP)

        // Moving
        chessBoard.board[toRow][toCol] = chessBoard.board[fromRow][fromCol]
        chessBoard.board[fromRow][fromCol] = null
        isPassed = (toRow - fromRow) * direction == 2
        colPassed = fromCol
    }
}

class ChessPiece( color: Char ) {
    var color: Char
    init {
        if (color.equals('b', true)) this.color = 'B'
        else if (color.equals('w', true)) this.color = 'W'
        else throw ChessException("Wrong piece color!")
    }
}