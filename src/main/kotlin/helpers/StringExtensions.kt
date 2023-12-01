package helpers

object StringExtensions {
    val NON_INT_DELIMITER = """(\D)+""".toRegex()
}

fun String.splitOnEmpty() = this.split(System.lineSeparator() + System.lineSeparator())
fun String.extractInts() = this.split(StringExtensions.NON_INT_DELIMITER).mapNotNull(String::toIntOrNull)

fun CharSequence.indexOfOrNull(other: String) = indexOf(other).takeIf { it != -1 }
fun CharSequence.lastIndexOfOrNull(other: String) = lastIndexOf(other).takeIf { it != -1 }
