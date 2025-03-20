package kds

import org.jline.reader.*
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import org.jline.reader.EndOfFileException
import org.jline.reader.UserInterruptException

fun main() {
    val terminal = TerminalBuilder.builder()
        .name("neuromicrok")
        .build()

    val lineReader = LineReaderBuilder.builder()
        .completer(StringsCompleter(listOf("/hello", "/exit")))
        .terminal(terminal)


        .appName("neuromicrok")
        .build()

    while (true) {
        try {
            val line = lineReader.readLine("neuromicrok> ").trim()
            if (line.equals("/hello")) {
                println("Hello, World!")
            } else if (line.equals("/exit")) {
                break
            }
             else {
                println("Unknown command: $line")

            }
        }   catch (e: EndOfFileException) {
            println("Exiting...")
            return
        } catch (e: Exception) {
            println("Error: ${e.message}")
            break
        }
    }
    println("Exiting...")
}
