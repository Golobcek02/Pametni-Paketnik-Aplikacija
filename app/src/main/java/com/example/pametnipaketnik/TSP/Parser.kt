package parsing

import java.io.File

class Parser(val string: String){

    fun parse(): List<String> {
        val file = File(string)
        val lines = mutableListOf<String>()
        file.useLines { lines.addAll(it) }
        return lines
    }
}