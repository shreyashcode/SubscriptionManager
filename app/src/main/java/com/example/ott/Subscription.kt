package com.example.ott

class Subscription(
    var Name: String,
    var Cost: Int,
    var FavouriteShow: String,
    var RenewDate: Long = 0
) {
    override fun toString(): String {
        return "$Name | $Cost | "
    }
}