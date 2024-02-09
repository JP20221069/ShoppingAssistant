package com.jspj.shoppingassistant.Utils

class StringEdit {

    companion object {
        public fun ShortenString(text: String, after: Int): String {
            var ret = text.substring(0, after);
            ret = ret + "...";
            return ret;
        }
    }
}