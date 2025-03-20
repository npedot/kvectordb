package kds

import org.jline.reader.*
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import org.jline.reader.EndOfFileException

const val APP_NAME = "kvectordb"

fun main() {
    val db = VectorDatabase()

    val terminal = TerminalBuilder.builder()
        .name(APP_NAME)
        .build()

    val lineReader = LineReaderBuilder.builder()
        .completer(StringsCompleter(listOf("/hello", "/quit", "/documents")))
        .terminal(terminal)
        .appName(APP_NAME)
        .build()

    while (true) {
        try {
            val line = lineReader.readLine("$APP_NAME> ").trim()
            if (line.equals("/hello")) {
                println("Hello, World!")
            } else if (line.equals("/quit")) {
                break
            } else if (line.equals("/documents")) {
                db.getAllDocuments().forEach {
                    println("Document ID: ${it.id}, Text: ${it.text}")
                }
            } else {
                println("Unknown command: $line")
            }
        } catch (e: EndOfFileException) {
            println("Quitting...")
            return
        } catch (e: Exception) {
            println("Error: ${e.message}")
            break
        }
    }
    println("Quitting...")
}
