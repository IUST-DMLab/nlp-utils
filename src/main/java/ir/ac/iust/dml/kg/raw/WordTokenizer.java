/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordTokenizer {

  private static final Logger logger = LoggerFactory.getLogger(WordTokenizer.class);
  private static MainWordTokenizer wordTokenizer;

  static {
    try {
      logger.info("creating word tokenizer class of jhazm");
      wordTokenizer = new MainWordTokenizer();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<List<String>> tokenizeRaw(String raw) {
    final List<String> sentences = SentenceTokenizer.SentenceSplitterRaw(raw);
    final List<List<String>> result = new ArrayList<>();
    sentences.forEach(it -> result.add(tokenize(it)));
    return result;
  }

  public static List<String> tokenize(String sentence) {
    logger.trace("word tokenizing for " + sentence);
    return wordTokenizer.tokenize(sentence);
  }
}
