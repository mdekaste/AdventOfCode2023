package helpers

fun Long.lcm(other: Long) = this * other / gcd(other)

tailrec fun Long.gcd(other: Long): Long = when (other) {
    0L -> this
    else -> other.gcd(this % other)
}
