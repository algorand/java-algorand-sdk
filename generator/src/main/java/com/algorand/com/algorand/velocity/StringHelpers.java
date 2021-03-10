package com.algorand.com.algorand.velocity;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class StringHelpers {
    public String kebabToCamel(String kebab) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, kebab);
    }

    /**
     * Removes the first and last character, i.e. if it's wrapped in "{string}" then "string" is returned.
     */
    public String unwrap(String wrapped) {
        return wrapped.substring(1, wrapped.length()-1);
    }

    public Boolean matches(String str, String pattern) {
        Pattern p = Pattern.compile(pattern);
        return p.matcher(str).matches();
    }

    public String capitalize(String str) {
        return StringUtils.capitalize(str);
    }
}
