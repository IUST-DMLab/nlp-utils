/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw;

import java.io.IOException;

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
