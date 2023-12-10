package helpers

import kotlin.math.max
import kotlin.math.min

operator fun LongRange.plus(amount: Long): LongRange = first + amount..last + amount
infix fun LongRange.intersect(other: LongRange) = max(first, other.first)..min(last, other.last)


