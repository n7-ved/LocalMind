package com.n7.localmind.core.sentence.embedding.model.tokenizer

/**
 * Defines the structure for the output of a tokenizer, containing the necessary inputs
 * for a transformer model.
 */
data class ModelInput(
    val inputIds: LongArray,
    val attentionMask: LongArray,
    val tokenTypeIds: LongArray
) {
    // Override equals and hashCode for proper comparison if needed, especially for LongArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelInput

        if (!inputIds.contentEquals(other.inputIds)) return false
        if (!attentionMask.contentEquals(other.attentionMask)) return false
        if (!tokenTypeIds.contentEquals(other.tokenTypeIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputIds.contentHashCode()
        result = 31 * result + attentionMask.contentHashCode()
        result = 31 * result + tokenTypeIds.contentHashCode()
        return result
    }
}

/**
 * Interface for a text tokenizer that prepares input for a sentence embedding model.
 */
interface Tokenizer {
    /**
     * Tokenizes the input text and prepares the model inputs (IDs, mask, type IDs).
     *
     * @param text The input string to tokenize.
     * @param maxSeqLength The maximum sequence length to pad/truncate to.
     * @return A [ModelInput] object containing the required LongArray inputs for the model.
     */
    fun tokenize(text: String, maxSeqLength: Int): ModelInput
} 