package day22

import Challenge
import helpers.extractInts

fun main() {
    Day22.part1().let(::println)
    Day22.part2().let(::println)
}

typealias XYZCoord = List<Int>

private operator fun <E> List<E>.component6() = get(5)

object Day22 : Challenge() {
    val parsed = input.lines().mapIndexed { index, s ->
        s.extractInts().let { (x1, y1, z1, x2, y2, z2) -> Brick(y1, x1, z1, y2, x2, z2) }
    }.sortedWith(compareBy(Brick::fromZ)).let {
        buildMap<Brick, List<Brick>> {
            for(brick in it){
                brick.moveDown()
            }
        }
    }

    override fun part1(): Any? {
        val fallenBricks = parsed.toMutableMap()
        val cannotRemove = fallenBricks.filter { it.value.size == 1 }.map { it.value[0] }.toSet()
        val canRemove = fallenBricks.filter { it.value.size >= 2 }.flatMap{ it.value }.toSet()
        val isOnTop = fallenBricks.filter { (key, _) -> key !in fallenBricks.values.flatten().toSet() }.keys
        return (canRemove - cannotRemove + isOnTop).size
    }

    override fun part2(): Any? {
        val fallenBricks = parsed.toMutableMap()
        val mapToCheck = fallenBricks - fallenBricks.filter { it.value.isEmpty() }.keys
        return fallenBricks.keys
            .map { remove(setOf(it), mapToCheck.mapValues { (_, value) -> value.toMutableSet() }.toMutableMap()) }
            .sumOf { it.size }
    }

    private fun remove(bricks: Set<Brick>, map: MutableMap<Brick, MutableSet<Brick>>): Set<Brick> {
        if(bricks.isEmpty())
            return emptySet()
        val mapIterator = map.iterator()
        var nextFall = mutableSetOf<Brick>()
        while(mapIterator.hasNext()){
            mapIterator.next().let {
                it.value -= bricks
                if(it.value.isEmpty()){
                    mapIterator.remove()
                    nextFall.add(it.key)
                }
            }
        }
        return nextFall + remove(nextFall, map)
    }
}

fun List<Point3D>.toBrick(height: Int): Brick {
    val minY = minOf { it.y }
    val minX = minOf { it.x }
    val minZ = minOf { it.z }

    val maxY = maxOf { it.y }
    val maxX = maxOf { it.x }
    val maxZ = minZ + height - 1
    return Brick(minY, minX, minZ, maxY, maxX, maxZ)
}

data class Brick(
    val fromY: Int,
    val fromX: Int,
    val fromZ: Int,
    val toY: Int,
    val toX: Int,
    val toZ: Int
) {
    val height = toZ - fromZ + 1
    context(MutableMap<Brick, List<Brick>>)
    fun moveDown() {
        val bottom = bottom()
        var z = bottom.first().z
        var prevBottom = bottom
        var curBottom = bottom
        while (z >= 1) {
            val touchingBricks = keys.filter { b -> curBottom.any { it in b } }
            if(touchingBricks.isNotEmpty()){
                put(prevBottom.toBrick(height), touchingBricks)
                return
            }
            prevBottom = curBottom
            curBottom = curBottom.map { it.doMoveDown() }
            z--
        }
        put(prevBottom.toBrick(height), emptyList())
    }

    operator fun contains(point3D: Point3D): Boolean {
        return point3D.z in fromZ..toZ && point3D.x in fromX..toX && point3D.y in fromY..toY
    }

    fun bottom(): List<Point3D> {
        val bottomZ = fromZ
        val layer = (fromY..toY).flatMap { y -> (fromX..toX).map { x -> Point3D(y, x, bottomZ) } }
        return layer
    }
}

data class Point3D(
    val y: Int,
    val x: Int,
    val z: Int
){
    fun doMoveDown() = copy(z = z - 1)
}