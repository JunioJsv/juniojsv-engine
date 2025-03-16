package juniojsv.engine.features.utils

import kotlin.math.pow

/**
 * Represents a scale of units, such as centimeters, meters, or kilometers.
 * Allows conversion between different scales.
 *
 * @property value The scale value. A value of 1.0f represents the base unit (meter).
 *
 */
class Scale private constructor(val value: Float) {

    companion object {
        /**
         * The base unit. For this engine we will use meter.
         */
        val METER = Scale(1.0f)

        /**
         * Centimeter scale (1/100th of a meter).
         */
        val CENTIMETER = Scale(0.01f)

        /**
         * Kilometer scale (1000 meters).
         */
        val KILOMETER = Scale(1000.0f)

        /**
         * Get a scale from the power of ten value.
         */
        fun fromPowerOfTen(power: Int) = Scale(10f.pow(power))

    }

    /**
     * Converts a length from this scale to another scale.
     *
     * @param length The length in this scale.
     * @param to The target scale.
     * @return The length in the target scale.
     */
    fun length(length: Float = value, to: Scale = METER): Float {
        return (length * value) / to.value
    }
}