package com.algorand.velocity;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringHelpers {
    /**
     * Convert from kebab case to "lowerCammelCase".
     */
    public String kebabToCamel(String kebab) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, kebab);
    }

    /**
     * Convert from kebab case to "UpperCammelCase".
     */
    public String kebabToUpperCamel(String kebab) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, kebab);
    }

    /**
     * Convert from kebab case to "lower_underscore_case".
     */
    public String kebabToUnderscore(String kebab) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, kebab);
    }

    /**
     * Convert from UpperCamelCase to "lower_underscore_case".
     */
    public String upperCamelToUnderscore(String kebab) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, kebab);
    }

    /**
     * Removes the first and last character, i.e. if it's wrapped in "{string}" then "string" is returned.
     */
    public String unwrap(String wrapped) {
        return wrapped.substring(1, wrapped.length()-1);
    }

    /**
     * Check if a pattern matches a string.
     */
    public Boolean matches(String str, String pattern) {
        Pattern p = Pattern.compile(pattern);
        return p.matcher(str).matches();
    }

    /**
     * Capitalize the first letter.
     */
    public String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * Un-capitalize the first letter.
     */
    public String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }


    /**
     * Formats documentation by splitting newlines and adding any required prefix and postfix.
     */
    public String formatDoc(String comment, String sep) {
        comment = comment.replace("\\[", "(");
        comment = comment.replace("\\]", ")");

        StringBuilder sb = new StringBuilder();
        String newline = "";
        for (String line : lineSplit(comment, 80)) {
            sb.append(newline + line);
            newline = "\n" + sep;
        }

        return sb.toString();
    }

    private List<String> lineSplit(String comment, int lineLimit) {
        List<String> result = new ArrayList<>();

        // To avoid the weird __NEWLINE__ thing we should be able to use 'new StringTokenizer(comment, " \t\f")'
        // StringTokenizer st = new StringTokenizer(comment, " \t\f");
        comment = comment.replace("\n", " __NEWLINE__ ");
        StringTokenizer st = new StringTokenizer(comment);
        StringBuilder sb = new StringBuilder();
        int line = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.contentEquals("__NEWLINE__") || token.contentEquals("\n")) {
                if (line != 0) {
                    line = 0;
                    result.add(sb.toString());
                    sb.setLength(0);
                }
                continue;
            }
            if (line + token.length() > lineLimit) {
                line = 0;
                result.add(sb.toString());
                sb.setLength(0);
            }
            // Option to remove weird double-star in javadoc?
            //if (comStr.indexOf("*") != -1) {
            //    token = token.replace('*', ' ');
            //}
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(token);
            line += token.length() + 1;
        }
        if (line > 0) {
            result.add(sb.toString());
        }
        return result;
    }
}
