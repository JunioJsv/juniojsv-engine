package juniojsv.engine.features.utils

enum class MovementDirection {
    FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
}

interface IMovable {
    fun move(movements: Set<MovementDirection>)
}