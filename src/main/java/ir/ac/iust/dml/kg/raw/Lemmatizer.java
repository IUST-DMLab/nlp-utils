package ir.ac.iust.dml.kg.raw;

import java.io.IOException;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 4/13/17 3:08 PM
 */
public class Lemmatizer {
    static ir.ac.iust.nlp.jhazm.Lemmatizer lemmatizer;

    static {
        try {
            lemmatizer = new ir.ac.iust.nlp.jhazm.Lemmatizer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String lemmatize(String token, String tag) {
        return lemmatizer.lemmatize(token, tag);
    }
}
