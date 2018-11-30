package com.eram.roundvisualizer

/**
 * Created by Mohammad Reza Eram (https://github.com/mreram) on 26,October,2018
 */
internal class RenderUtils {
    companion object {

        private const val SHIFT = java.lang.Byte.MAX_VALUE


        fun sum(data: ByteArray): Int {
            var sum = 0
            for (b in data)
                sum += b.toInt()
            return sum
        }


        fun toAmplitude(b: Byte): Int {
            return if (b > 0) b + SHIFT else -b//+127=high positive;+1=low positive;-127=low negative;-1=high negative
        }

        fun toAmplitude(f: Float): Float {
            return if (f > 0) f + SHIFT else -f//+127=high positive;+1=low positive;-127=low negative;-1=high negative
        }
    }
}