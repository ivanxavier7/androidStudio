package com.ptda.imiser.helper;

import android.util.Base64;

public class Base64Custom {
    public static String encodeBase64Custom(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    public static String decodeBase64Custom(String encoded_text){
        return new String( Base64.decode(encoded_text, Base64.DEFAULT) );
    }
}
