package app.zornslemma.mypricelog.common

fun <T> intersectionIsEmpty(lhs: Set<T>, rhs: Set<T>) = !(lhs.any { it in rhs })
