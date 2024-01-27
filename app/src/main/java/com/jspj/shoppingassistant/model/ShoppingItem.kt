package com.jspj.shoppingassistant.model

class ShoppingItem(var Product:Product?=null, var Amount:Int=-1, var Notes:String="",var Checked:Boolean=false)
{
    constructor( Product:Product,Amount:Int) : this(Product,Amount,"") {
    }

}