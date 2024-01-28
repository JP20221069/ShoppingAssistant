package com.jspj.shoppingassistant.model

class Store (var ID:Int=-1,var Name:String=""){

    override fun toString(): String {
        return Name
    }
}