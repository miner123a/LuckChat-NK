// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo.util;

public class Strings
{
    public static String format(final String template, final Object... args) {
        if (args.length == 0 || template.length() == 0) {
            return template;
        }
        final char[] arr = template.toCharArray();
        final StringBuilder stringBuilder = new StringBuilder(template.length());
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] == '{' && Character.isDigit(arr[Math.min(i + 1, arr.length - 1)]) && arr[Math.min(i + 1, arr.length - 1)] - '0' < args.length && arr[Math.min(i + 2, arr.length - 1)] == '}') {
                stringBuilder.append(args[arr[i + 1] - '0']);
                i += 2;
            }
            else {
                stringBuilder.append(arr[i]);
            }
        }
        return stringBuilder.toString();
    }
    
    public static String format(final String template, final String... args) {
        return format(template, (Object[])args);
    }
}
