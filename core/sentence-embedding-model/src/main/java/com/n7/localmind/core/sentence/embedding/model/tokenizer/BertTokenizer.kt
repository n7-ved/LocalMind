package com.n7.localmind.core.sentence.embedding.model.tokenizer

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

// Data class defined within the Tokenizer interface
// data class ModelInput(
//     val inputIds: LongArray,
//     val attentionMask: LongArray,
//     val tokenTypeIds: LongArray
// )

// Interface is assumed to be defined elsewhere, e.g., in OnnxEmbeddingModelImpl or its own file
// interface Tokenizer {
//     fun tokenize(text: String, maxSeqLength: Int): ModelInput
// }


/**
 * Implements WordPiece tokenization commonly used by BERT models.
 * Loads vocabulary from the assets directory.
 */
class BertTokenizer(
    context: Context,
    // Directory within assets containing vocab.txt
    assetsDir: String,
    // Vocabulary file name
    private val vocabFile: String = "vocab.txt"
) : Tokenizer {

    private val vocab: MutableMap<String, Int> = mutableMapOf()
    private val invVocab: MutableMap<Int, String> = mutableMapOf() // Optional: useful for debugging

    // Standard BERT special tokens
    private val clsToken = "[CLS]"
    private val sepToken = "[SEP]"
    private val padToken = "[PAD]"
    private val unkToken = "[UNK]"

    private val clsTokenId: Int
    private val sepTokenId: Int
    private val padTokenId: Int
    private val unkTokenId: Int

    init {
        // Construct path correctly whether assetsDir is empty or not
        val vocabPath = if (assetsDir.isNotEmpty()) {
            "$assetsDir/$vocabFile"
        } else {
            vocabFile // Use filename directly if assetsDir is empty
        }
        println("Loading vocabulary from assets: $vocabPath")
        try {
            context.assets.open(vocabPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    var index = 0
                    reader.forEachLine { line ->
                        val trimmedLine = line.trim()
                        if (trimmedLine.isNotEmpty()) {
                            vocab[trimmedLine] = index
                            invVocab[index] = trimmedLine // Populate inverse map
                            index++
                        }
                    }
                }
            }
            println("Vocabulary loaded successfully. Size: ${vocab.size}")

            // Get IDs for special tokens, throw error if not found
            clsTokenId = vocab[clsToken] ?: throw IllegalStateException("'$clsToken' not found in vocabulary: $vocabPath")
            sepTokenId = vocab[sepToken] ?: throw IllegalStateException("'$sepToken' not found in vocabulary: $vocabPath")
            padTokenId = vocab[padToken] ?: vocab.getOrDefault("[PAD]", 0) // Common default for PAD is 0
            unkTokenId = vocab[unkToken] ?: throw IllegalStateException("'$unkToken' not found in vocabulary: $vocabPath")

        } catch (e: Exception) {
            println("Error loading vocabulary from $vocabPath: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("Failed to initialize BertTokenizer", e)
        }
    }

    override fun tokenize(text: String, maxSeqLength: Int): ModelInput {
        // 1. Basic Preprocessing (lowercase, basic whitespace split)
        // More sophisticated preprocessing (like stripping accents, handling punctuation)
        // might be needed depending on the exact model training.
        val cleanedText = text.lowercase() // Common practice for many BERT models
        val initialTokens = cleanedText.split(Regex("\\s+")).filter { it.isNotEmpty() }

        // 2. WordPiece Tokenization
        val wordPieceTokens = mutableListOf<String>()
        wordPieceTokens.add(clsToken) // Start with [CLS]

        for (token in initialTokens) {
            // If the whole token is in vocab, add it directly
            if (vocab.containsKey(token)) {
                wordPieceTokens.add(token)
            } else {
                // Apply WordPiece algorithm
                var currentToken = ""
                val subTokens = mutableListOf<String>()
                for (char in token) {
                    val subToken = currentToken + char
                    if (vocab.containsKey(subToken)) {
                        currentToken = subToken
                    } else {
                        // Check if the subtoken with '##' prefix exists
                        val subTokenWithPrefix = "##$subToken"
                        if (vocab.containsKey(subTokenWithPrefix)) {
                             currentToken = subTokenWithPrefix // Use the prefixed version
                        } else {
                            // If neither exists, add the previous best subtoken (if any)
                            // and start a new subtoken, possibly marking the current char as UNK
                            // if it doesn't start a known subword.
                            if (currentToken.isNotEmpty() && currentToken != "##") {
                                subTokens.add(currentToken)
                            }
                             // Start new subtoken with current char, check if known or UNK
                             val charStr = char.toString()
                             currentToken = if (vocab.containsKey(charStr)) charStr else unkToken
                        }
                    }
                }
                 // Add the last accumulated subtoken
                 if (currentToken.isNotEmpty()) {
                      subTokens.add(currentToken)
                 }

                 // If no subtokens were generated (e.g., all chars were unknown), add UNK
                 if (subTokens.isEmpty()) {
                    wordPieceTokens.add(unkToken)
                 } else {
                    wordPieceTokens.addAll(subTokens)
                 }
            }
            // Truncate tokens early if already exceeding limit minus [SEP]
            if (wordPieceTokens.size >= maxSeqLength - 1) {
                break
            }
        }

        // 3. Truncate if necessary (account for [CLS] and [SEP])
        val effectiveMaxLength = maxSeqLength - 2 // Space for [CLS] and [SEP]
        val truncatedTokens = wordPieceTokens.take(effectiveMaxLength + 1) // +1 because we already added [CLS]
           .toMutableList() // Make mutable to add [SEP]

        truncatedTokens.add(sepToken) // Add [SEP] at the end

        // 4. Convert tokens to IDs
        val currentLength = truncatedTokens.size
        val inputIdsInt = truncatedTokens.map { vocab.getOrDefault(it, unkTokenId) }.toMutableList()

        // 5. Create Attention Mask (1 for real tokens, 0 for padding)
        val attentionMaskInt = MutableList(currentLength) { 1 }

        // 6. Padding
        val paddingLength = maxSeqLength - currentLength
        if (paddingLength > 0) {
            repeat(paddingLength) {
                inputIdsInt.add(padTokenId)
                attentionMaskInt.add(0) // Mask for padding tokens
            }
        }

        // 7. Create Token Type IDs (Segment IDs) - All 0 for single sentences
        val tokenTypeIdsInt = MutableList(maxSeqLength) { 0 }

        // 8. Convert to LongArray (as expected by the ONNX model int64 type)
        val inputIdsLong = inputIdsInt.map { it.toLong() }.toLongArray()
        val attentionMaskLong = attentionMaskInt.map { it.toLong() }.toLongArray()
        val tokenTypeIdsLong = tokenTypeIdsInt.map { it.toLong() }.toLongArray()

        // Debugging output (optional)
        // println("--- Tokenization ---")
        // println("Text: $text")
        // println("Tokens: ${truncatedTokens.joinToString(" ")}")
        // println("Input IDs: ${inputIdsLong.joinToString(" ")}")
        // println("Attn Mask: ${attentionMaskLong.joinToString(" ")}")
        // println("Type IDs:  ${tokenTypeIdsLong.joinToString(" ")}")
        // println("--------------------")

        return ModelInput(inputIdsLong, attentionMaskLong, tokenTypeIdsLong)
    }
}

