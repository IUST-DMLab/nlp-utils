/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SentenceTokenizer {

  private static final Logger logger = LoggerFactory.getLogger(SentenceTokenizer.class);
  private static MainSentenceSplitter sentenceTokenizer = new MainSentenceSplitter();

  public static List<String> SentenceSplitterRaw(String text) {
    return SentenceSplitter(Normalizer.normalize(text));
  }

  public static List<String> SentenceSplitter(String text) {
    logger.trace("tokenize to sentences: " + text);
    return sentenceTokenizer.tokenize(text);
  }

}
