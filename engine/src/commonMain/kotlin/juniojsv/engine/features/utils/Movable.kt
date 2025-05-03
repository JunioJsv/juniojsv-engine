package juniojsv.engine.features.utils

enum class MovementDirection {
    FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN;

    companion object {
        fun from(x: Float, y: Float): Set<MovementDirection> {
            val directions = mutableSetOf<MovementDirection>()
            if (x > 0) directions.add(RIGHT)
            if (x < 0) directions.add(LEFT)
            if (y > 0) directions.add(FORWARD)
            if (y < 0) directions.add(BACKWARD)
            return directions
        }
    }
}

interface IMovable {
    fun move(movements: Set<MovementDirection>)
    fun move(x: Float, y: Float)
}