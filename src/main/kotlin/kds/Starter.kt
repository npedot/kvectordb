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
        .completer(StringsCompleter(listOf("/hello", "/quit", "/documents", "/insert", "/search", "/vector")))
        .terminal(terminal)
        .appName(APP_NAME)
        .build()

    // Example usage:
    // To insert a document with ID "doc6" and text "This is a new document", type:
    // /insert doc6 This is a new document
    // To search documents with query "semantic search", type:
    // /search semantic search
    // To calculate the vector embedding of a text, type:
    // /vector <text>

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
            } else if (line.startsWith("/insert")) {
                val parts = line.split(" ", limit = 3)
                if (parts.size == 3) {
                    val id = parts[1]
                    val text = parts[2]
                    val document = db.addDocument(id, text)
                    println("Document added: ID: ${document.id}, Text: ${document.text}")
                } else {
                    println("Invalid command format. Usage: /insert <id> <text>")
                }
            } else if (line.startsWith("/search")) {
                val query = line.substringAfter("/search").trim()
                val results = db.search(query)
                if (results.isEmpty()) {
                    println("No results found for query: $query")
                } else {
                    println("Search results for query: $query")
                    results.forEachIndexed { index, (document, score) ->
                        println("${index + 1}. [ID: ${document.id}, Score: ${String.format("%.4f", score)}] ${document.text}")
                    }
                }
            } else if (line.startsWith("/search")) {
                val query = line.substringAfter("/search").trim()
                if (query.isEmpty()) {
                    println("Usage: /search <query>")
                }
                else {
                    val results = db.search(query)
                    if (results.isEmpty()) {
                        println("No results found for query: $query")
                    } else {
                        println("Search results for query: $query")
                        results.forEachIndexed { index, (document, score) ->
                            println("${index + 1}. [ID: ${document.id}, Score: ${String.format("%.4f", score)}] ${document.text}")
                        }
                    }
                }
            } else if (line.startsWith("/vector")) {
                val text = line.substringAfter("/vector").trim()
                if (text.isEmpty()) {
                    println("Usage: /vector <text>")
                } else {
                    val embeddingService = EmbeddingService()
                    val vector = embeddingService.textToVector(text)
                    println("Vector embedding for text: $text")
                    println(vector.contentToString())
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
