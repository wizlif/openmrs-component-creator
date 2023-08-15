package io.github.wizlif.mets.mets.actions

fun String.getSnakeCase(separator: CharSequence = "_"): String {
    return words.joinToString(separator = separator).lowercase()
}

fun String.getPascalCase(separator: CharSequence = ""): String {
    return words.joinToString(separator) { it.upperCaseFirstLetter }
}

private val String.upperCaseFirstLetter: String
    get() {
        return "${substring(0, 1).uppercase()}${substring(1).lowercase()}"
    }

val symbolSet = setOf(' ', '.', '/', '_', '\\', '-')
val upperAlphaRegex = Regex("[A-Z]")

private val String.words: List<String>
    get() {
        val sb = StringBuilder();
        val words = mutableListOf<String>()
        val isAllCaps = this.uppercase() == this

        for ((i, char) in this.withIndex()) {

            val nextChar: Char? = if (i + 1 == this.length) null else this[i + 1]

            if (symbolSet.contains(char)) {
                continue
            }

            sb.append(char)

            val isEndOfWord = nextChar == null ||
                    (upperAlphaRegex.matches(nextChar.toString()) && !isAllCaps) ||
                    symbolSet.contains(nextChar)

            if (isEndOfWord) {
                words.add(sb.toString());
                sb.clear()
            }
        }

        return words
    }