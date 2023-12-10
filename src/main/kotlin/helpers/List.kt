package helpers

operator fun <T> List<T>.component6() = get(5)
operator fun <T> List<T>.component7() = get(6)

fun <T1, T2> List<T1>.cartesianProduct(a: List<T2>) = flatMap { t1 ->
    a.map { t2 -> t1 to t2 }
}

fun <T1, T2, R> List<T1>.cartesianProduct(a: List<T2>, function: (T1, T2) -> R) = flatMap { t1 ->
    a.map { t2 -> function(t1, t2) }
}

fun <T1, T2, T3> List<T1>.cartesianProduct(a: List<T2>, b: List<T3>) = flatMap { t1 ->
    a.flatMap { t2 ->
        b.map { t3 -> Triple(t1, t2, t3) }
    }
}

fun <T1, T2, T3, R> List<T1>.cartesianProduct(a: List<T2>, b: List<T3>, function: (T1, T2, T3) -> R) = flatMap { t1 ->
    a.flatMap { t2 ->
        b.map { t3 ->
            function(t1, t2, t3)
        }
    }
}

