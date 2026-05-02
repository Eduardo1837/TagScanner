package com.example.tagscanner.domain.model

data class RgbColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    init {
        require(red in 0..255){
            "Red value needed between 0..255"
        }
        require(green in 0..255){
            "Green value needed between 0..255"
        }
        require(blue in 0..255){
            "Blue values needed between 0..255"
        }
    }
}