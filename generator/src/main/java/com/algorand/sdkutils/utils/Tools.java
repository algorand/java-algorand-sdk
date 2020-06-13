package com.algorand.sdkutils.utils;

import java.util.StringTokenizer;

public class Tools {

    // formatComment formats the comment by breaking lines, and incorporating 
    // embedded formatting inside the comment. 
    // If the comment is embedded inside another comment, full == false
    public static String formatComment(String comment, String tab, boolean full) {
        if (comment == null || comment.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();

        comment = comment.replace("\\[", "(");
        comment = comment.replace("\\]", ")");
        comment = comment.replace("\n", " __NEWLINE__ ");
        if (full) {
            sb.append(tab+"/**");
            sb.append("\n"+tab+" *");
        } else {
            sb.append(tab+" *");
        }

        StringTokenizer st = new StringTokenizer(comment);
        int line = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.contentEquals("__NEWLINE__")) {
                if (line == 0) {
                    continue;
                } else {
                    line = 0;
                    sb.append("\n"+tab+" *");
                    continue;
                }
            }
            if (line + token.length() > 80) {
                line = 0;
                sb.append("\n"+tab+" *");
            }
            token = token.replace('*', ' ');
            sb.append(" ");

            sb.append(token);
            line += token.length() + 1;
        }
        if (full) {
            sb.append("\n"+tab+" */\n");
        }
        return sb.toString();
    }

    public static String getCamelCase(String name, boolean firstCap) {
        boolean capNext = firstCap;
        char [] newName = new char[name.length()+1];
        int n = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '-') {
                capNext = true;
                continue;
            }
            if (capNext) {
                newName[n++] = Character.toUpperCase(name.charAt(i));
                capNext = false;
            } else {
                newName[n++] = name.charAt(i);
            }
        }
        return new String(newName, 0, n);
    }
}
