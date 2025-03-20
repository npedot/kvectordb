package kds

/**
 * Mini Database Vettoriale in Kotlin
 *
 * Questo esempio dimostra i concetti fondamentali di un database vettoriale:
 * - Rappresentazione di documenti come vettori
 * - Calcolo della similarità coseno
 * - Indicizzazione e ricerca vettoriale
 */

import kotlin.math.sqrt

/**
 * Rappresenta un documento con il suo testo originale e il vettore associato.
 */
data class Document(
    val id: String,
    val text: String,
    val vector: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (id != other.id) return false
        if (text != other.text) return false
        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + vector.contentHashCode()
        return result
    }
}

/**
 * Servizio di embedding che converte testo in vettori.
 * In un caso reale, questo utilizzerebbe un modello di ML per la conversione.
 */
class EmbeddingService {
    /**
     * Converte una stringa di testo in un vettore di embedding.
     * Questa è una versione semplificata che simula l'embedding.
     *
     * @param text Il testo da convertire in vettore
     * @return Un vettore di dimensione 5 che rappresenta il testo
     */
    fun textToVector(text: String): DoubleArray {
        // In un'implementazione reale, qui utilizzeremmo un modello di ML
        // Questa è una semplice simulazione che crea un vettore basato sui caratteri
        val vector = DoubleArray(5) { 0.0 }

        // Utilizziamo un algoritmo semplice basato sui caratteri per generare i vettori
        text.toLowerCase().forEachIndexed { index, char ->
            val charCode = char.code.toDouble()
            val position = index % vector.size
            vector[position] += charCode / 1000.0
        }

        // Normalizziamo il vettore
        val magnitude = sqrt(vector.map { it * it }.sum())
        if (magnitude > 0) {
            for (i in vector.indices) {
                vector[i] /= magnitude
            }
        }

        return vector
    }
}

/**
 * Il database vettoriale che memorizza i documenti e fornisce funzionalità di ricerca.
 */
class VectorDatabase {
    private val documents = mutableListOf<Document>()
    private val embeddingService = EmbeddingService()

    /**
     * Aggiunge un documento al database.
     *
     * @param id L'identificatore del documento
     * @param text Il testo del documento
     * @return Il documento aggiunto
     */
    fun addDocument(id: String, text: String): Document {
        val vector = embeddingService.textToVector(text)
        val document = Document(id, text, vector)
        documents.add(document)
        return document
    }

    /**
     * Cerca documenti simili a una query di testo.
     *
     * @param queryText Il testo di ricerca
     * @param limit Il numero massimo di risultati da restituire
     * @return Lista di coppie (documento, punteggio di similarità) ordinata per similarità
     */
    fun search(queryText: String, limit: Int = 5): List<Pair<Document, Double>> {
        val queryVector = embeddingService.textToVector(queryText)

        return documents
            .map { document -> Pair(document, cosineSimilarity(queryVector, document.vector)) }
            .sortedByDescending { it.second }
            .take(limit)
    }

    /**
     * Calcola la similarità coseno tra due vettori.
     *utilizzo
     * @param vec1 Il primo vettore
     * @param vec2 Il secondo vettore
     * @return Il valore di similarità coseno compreso tra -1 e 1
     */
    private fun cosineSimilarity(vec1: DoubleArray, vec2: DoubleArray): Double {
        if (vec1.size != vec2.size) {
            throw IllegalArgumentException("I vettori devono avere la stessa dimensione")
        }

        var dotProduct = 0.0
        var norm1 = 0.0
        var norm2 = 0.0

        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            norm1 += vec1[i] * vec1[i]
            norm2 += vec2[i] * vec2[i]
        }

        // Preveniamo la divisione per zero
        if (norm1 <= 0.0 || norm2 <= 0.0) {
            return 0.0
        }

        return dotProduct / (sqrt(norm1) * sqrt(norm2))
    }

    /**
     * Restituisce tutti i documenti nel database.
     */
    fun getAllDocuments(): List<Document> = documents.toList()

    /**
     * Restituisce il numero di documenti nel database.
     */
    fun size(): Int = documents.size
}

/**
 * Esempio di utilizzo del database vettoriale.
 */
fun main() {
    val db = VectorDatabase()

    // Aggiungiamo alcuni documenti al database
    println("Aggiungo documenti al database...")
    db.addDocument("doc1", "I database vettoriali sono utili per la ricerca semantica")
    db.addDocument("doc2", "I motori di ricerca utilizzano tecnologie di embedding")
    db.addDocument("doc3", "L'intelligenza artificiale rivoluziona il recupero delle informazioni")
    db.addDocument("doc4", "Le reti neurali generano embedding di alta qualità")
    db.addDocument("doc5", "La similarità coseno misura quanto due vettori sono simili")

    println("Documenti nel database: ${db.size()}")

    // Eseguiamo alcune ricerche
    println("\nRicerca 1: 'database semantici'")
    val results1 = db.search("database semantici")
    printSearchResults(results1)

    println("\nRicerca 2: 'intelligenza artificiale e reti neurali'")
    val results2 = db.search("intelligenza artificiale e reti neurali")
    printSearchResults(results2)

    println("\nRicerca 3: 'similarità tra vettori'")
    val results3 = db.search("similarità tra vettori")
    printSearchResults(results3)
}

/**
 * Stampa i risultati della ricerca in formato leggibile.
 */
fun printSearchResults(results: List<Pair<Document, Double>>) {
    results.forEachIndexed { index, (document, score) ->
        println("${index + 1}. [ID: ${document.id}, Score: ${String.format("%.4f", score)}] ${document.text}")
    }
}