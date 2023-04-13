fun calculateSquare(array: IntArray?, index: Int) {
    // write your code here
    if (array == null || index < 0 || index > array.size - 1) {
        println("Exception!")
    } else println( array[index] * array [index] )
}