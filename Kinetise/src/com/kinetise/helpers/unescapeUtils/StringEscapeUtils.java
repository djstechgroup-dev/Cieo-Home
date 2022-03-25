package com.kinetise.helpers.unescapeUtils;

import com.kinetise.helpers.escapeUtils.JavaUnicodeEscaper;

public class StringEscapeUtils {

    /**
     * Translator object for unescaping escaped Java. 
     * 
     * While {@link #unescapeJava(String)} is the expected method of use, this 
     * object allows the Java unescaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator UNESCAPE_JAVA =
        new AggregateTranslator(
            new OctalUnescaper(),     // .between('\1', '\377'),
            new UnicodeUnescaper(),
            new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE()),
            new LookupTranslator(
                      new String[][] { 
                            {"\\\\", "\\"},
                            {"\\\"", "\""},
                            {"\\'", "'"},
                            {"\\", ""}
                      })
        );
    
    /**
     * <p>Unescapes any Java literals found in the {@code String}.
     * For example, it will turn a sequence of {@code '\'} and
     * {@code 'n'} into a newline character, unless the {@code '\'}
     * is preceded by another {@code '\'}.</p>
     * 
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     */
    public static final String unescapeJava(final String input) {
        return UNESCAPE_JAVA.translate(input);
    }
    

    /**
     * Translator object for escaping Java. 
     * 
     * While {@link #escapeJava(String)} is the expected method of use, this 
     * object allows the Java escaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator ESCAPE_JAVA = 
          new LookupTranslator(
            new String[][] { 
              {"\"", "\\\""},
              {"\\", "\\\\"},
          }).with(
            new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE())
          ).with(
                  JavaUnicodeEscaper.outsideOf(32, 0x7f) 
        );
    
    // Java and JavaScript
    //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a {@code String} using Java String rules.</p>
     *
     * <p>Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     *
     * <p>So a tab becomes the characters {@code '\\'} and
     * {@code 't'}.</p>
     *
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote and forward-slash (/) are escaped.</p>
     *
     * <p>Example:
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     * </p>
     *
     * @param input  String to escape values in, may be null
     * @return String with escaped values, {@code null} if null string input
     */
    public static final String escapeJava(final String input) {
        return ESCAPE_JAVA.translate(input);
    }


    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     *
     * <p>For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;"
     * will become "&lt;Fran&ccedil;ais&gt;"</p>
     *
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will
     * become "&gt;&amp;zzzz;x".</p>
     *
     * @param str  the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeHtml(String)
     **/
    public static String unescapeHtml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.HTML40.unescape(str);
    }

    public static String unescapeXML(String str){
        if (str==null)
            return null;
        return Entities.XML.unescape(str);
    }
}
