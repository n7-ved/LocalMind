package com.n7.localmind.component.common.util

import android.util.Log

class ChunkGenerator {

    companion object {
        /**
         * Splits the document text into chunks using a sliding window approach.
         *
         * @param docText The full document text.
         * @param chunkSize The number of words in each chunk.
         * @param chunkOverlapPercentage The percentage (0-100) of words to overlap between consecutive chunks.
         * @param separatorParagraph The string to split paragraphs (default: "\n").
         * @param separator The string to split words (default: " ").
         * @return List of chunked strings.
         */
        fun createChunks(
            docText: String,
            chunkSize: Int,
            chunkOverlapPercentage: Int,
            separatorParagraph: String = "\n\n",
            separator: String = " "
        ): List<String> {
            val textChunks = ArrayList<String>()
            val paragraphs = docText.split(separatorParagraph)
            Log.d("Nick-ChunkGenerator", "Number of paragraphs: ${paragraphs.size}")
            for ((pIdx, paragraph) in paragraphs.withIndex()) {
                val words = paragraph.split(separator).filter { it.isNotBlank() }
                Log.d("Nick-ChunkGenerator", "Paragraph $pIdx: ${words.size} words")
                if (words.isEmpty()) continue
                val chunkOverlap = (chunkSize * chunkOverlapPercentage / 100).coerceAtLeast(0)
                Log.d(
                    "Nick-ChunkGenerator",
                    "chunkSize: $chunkSize, chunkOverlapPercentage: $chunkOverlapPercentage, chunkOverlap: $chunkOverlap"
                )
                var start = 0
                while (start < words.size) {
                    val end = (start + chunkSize).coerceAtMost(words.size)
                    val chunk = words.subList(start, end).joinToString(separator)
                    Log.d(
                        "Nick-ChunkGenerator",
                        "Chunk from $start to $end (size: ${end - start}): $chunk"
                    )
                    textChunks.add(chunk)
                    if (end == words.size) break
                    start += (chunkSize - chunkOverlap).coerceAtLeast(1)
                }
            }

            Log.d("Nick-ChunkGenerator", "Total chunks created: ${textChunks.size}")
            return textChunks
        }
    }
} 