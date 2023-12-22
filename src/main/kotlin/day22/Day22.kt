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
    }

    override fun part1(): Any? {
        val bricks = parsed.sortedWith(
            compareBy(Brick::fromZ).thenBy(Brick::toZ)
        ).toMutableList()
        val fallenBricks = mutableMapOf<Brick, List<Brick>>()
        while (bricks.isNotEmpty()) {
            val brickToFall = bricks.removeFirst()
            brickToFall.moveDown(fallenBricks.keys).let { (a, b) -> fallenBricks.put(a, b) }
        }
        val cannotRemove = fallenBricks.filter { it.value.size == 1 }.map { it.value[0] }.toSet()
        val canRemove = fallenBricks.filter { it.value.size >= 2 }.flatMap{ it.value }.toSet()
        val isOnTop = fallenBricks.filter { (key, _) -> key !in fallenBricks.values.flatten().toSet() }.keys
        return (canRemove - cannotRemove + isOnTop).distinct().size
    }

    override fun part2(): Any? {
        val bricks = parsed.sortedWith(
            compareBy(Brick::fromZ)
        ).toMutableList()
        val fallenBricks = mutableMapOf<Brick, List<Brick>>()
        while (bricks.isNotEmpty()) {
            val brickToFall = bricks.removeFirst()
            brickToFall.moveDown(fallenBricks.keys).let { (a, b) -> fallenBricks.put(a, b) }
        }
        val cannotRemove = fallenBricks.filter { it.value.size == 1 }.map { it.value[0] }.toSet()
        val canRemove = fallenBricks.filter { it.value.size >= 2 }.flatMap{ it.value }.toSet()
        val isOnTop = fallenBricks.filter { (key, _) -> key !in fallenBricks.values.flatten().toSet() }.keys
        val candidates = fallenBricks.filter { it.value.size == 0 }.keys.toSet()
        val mapToCheck = fallenBricks - fallenBricks.filter { it.value.size == 0 }.keys
        return fallenBricks.keys.map { remove(setOf(it), mapToCheck) }.sumOf { it.size }
    }

    fun remove(bricks: Set<Brick>, map: Map<Brick, List<Brick>>): Set<Brick> {
        if(bricks.isEmpty())
            return emptySet()
        var newMap = map.toMap().mapValues { it.value - bricks }.toMutableMap()
        val willFall = newMap.filter { it.value.isEmpty() }.keys
        newMap -= willFall
        return willFall + remove(willFall, newMap)
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
    fun moveDown(bricks: Set<Brick>): Pair<Brick, List<Brick>> {
        val bottom = bottom()
        var z = bottom.first().z
        var prevBottom = bottom
        var curBottom = bottom
        while (z >= 1) {
            val touchingBricks = bricks.filter { b -> curBottom.any { it in b } }
            if(touchingBricks.isNotEmpty()){
                return prevBottom.toBrick(height) to touchingBricks
            }
            prevBottom = curBottom
            curBottom = curBottom.map { it.doMoveDown() }
            z--
        }
        return prevBottom.toBrick(height) to emptyList()
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