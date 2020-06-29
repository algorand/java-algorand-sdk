package com.algorand.sdkutils.utils;

import java.lang.Character;
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
        if (full) {
            sb.append(tab+"/**");
            sb.append("\n"+tab+" *");
        } else {
            sb.append(tab+" *");
        }

        formatCommentImpl(sb, comment, " *", tab);

        if (full) {
            sb.append("\n"+tab+" */\n");
        }
        return sb.toString();
    }

    public static String formatCommentGo(String comment, String name, String tab) {
        if (comment == null || comment.isEmpty()) {
            return "";
        }
        String commentPossibleModified = comment;
        if (name.length() > 0 && !comment.startsWith(name)) {
            char firstChar = comment.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                firstChar = Character.toLowerCase(firstChar);
            }
            commentPossibleModified = name + " " + firstChar + comment.substring(1);
        }
        int endIndex = commentPossibleModified.length();
        if (commentPossibleModified.endsWith("\n") && endIndex > 2) {
            commentPossibleModified = commentPossibleModified.substring(0, endIndex-1);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(tab+"//");
        formatCommentImpl(sb, commentPossibleModified, "//", tab);
        sb.append("\n");
        return sb.toString();
    }

    private static void formatCommentImpl(
        StringBuffer sb, String comment,
        String comStr, String tab
    ) {
        comment = comment.replace("\\[", "(");
        comment = comment.replace("\\]", ")");
        comment = comment.replace("\n", " __NEWLINE__ ");

        StringTokenizer st = new StringTokenizer(comment);
        int line = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.contentEquals("__NEWLINE__")) {
                if (line == 0) {
                    continue;
                } else {
                    line = 0;
                    sb.append("\n"+tab+comStr);
                    continue;
                }
            }
            if (line + token.length() > 80) {
                line = 0;
                sb.append("\n"+tab+comStr);
            }
            if (comStr.indexOf("*") != -1) {
                token = token.replace('*', ' ');
            }
            sb.append(" ");

            sb.append(token);
            line += token.length() + 1;
        }
        return;
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
                if (!firstCap && n == 0 && i == 0) {
                    newName[n++] = name.substring(0,1).toLowerCase().charAt(0);
                } else {
                    newName[n++] = name.charAt(i);
                }
            }
        }
        return new String(newName, 0, n);
    }
}
