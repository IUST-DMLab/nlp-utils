/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParagraphSplitter {
    static ir.ac.iust.nlp.jhazm.Lemmatizer lemmatizer;

    static {
        try {
            lemmatizer = new ir.ac.iust.nlp.jhazm.Lemmatizer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> paragraphSplitter(String text) {
        String[] paragraphs = text.split("\n");
        List<String> paragraphList = new ArrayList<>();
        for (String paragraph : paragraphs) {
            if (paragraph.length() > 1) {
                paragraphList.add(paragraph);
            }
        }
        return paragraphList;


    }
}
