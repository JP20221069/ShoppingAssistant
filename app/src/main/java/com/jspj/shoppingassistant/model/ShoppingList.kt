package com.jspj.shoppingassistant.model

class ShoppingList(var ID:Int=-1,var Products:MutableList<ShoppingItem>?=null, var Name:String="", var Store:Store?=null)
{
    public fun addProduct(p:Product,amount:Int)
    {
        this.Products?.add(ShoppingItem(p,amount));
    }

    public fun editProduct(ListItemID:Int,newproduct:Product,newamount:Int)
    {
        Products?.set(ListItemID, ShoppingItem(newproduct,newamount));
    }

    public fun deleteProduct(ListItemID: Int)
    {
        Products?.removeAt(ListItemID);
    }

}