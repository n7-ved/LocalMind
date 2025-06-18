package com.n7.localmind.core.common.file

object CsvExporter {

    fun <T> exportListToCSV(
        data: List<T>,
        headers: List<String>? = null,
        rowMapper: (T) -> List<String>
    ): String {
        val csv = StringBuilder()

        headers?.let {
            csv.append(it.joinToString(",")).append("\n")
        }

        for (item in data) {
            val row = rowMapper(item).joinToString(",") { it.cleanupForCsv() }
            csv.append(row).append("\n")
        }

        return csv.toString()
    }

    private fun String.cleanupForCsv(): String {
        return this
            .replace("\"", "\"\"")
            .let {
                if (it.contains(",") || it.contains("\n")) "\"$it\"" else it
            }
    }
}
