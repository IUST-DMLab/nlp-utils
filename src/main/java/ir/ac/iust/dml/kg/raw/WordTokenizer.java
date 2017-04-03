package ir.ac.iust.dml.kg.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordTokenizer {

   private static final Logger logger = LoggerFactory.getLogger(WordTokenizer.class);
   private static ir.ac.iust.nlp.jhazm.WordTokenizer wordTokenizer;

   static {
      try {
         logger.info("creating word tokenizer class of jhazm");
         wordTokenizer = new ir.ac.iust.nlp.jhazm.WordTokenizer();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static List<List<String>> tokenizeRaw(String raw) {
      final List<String> sentences = SentenceTokenizer.tokenizeRaw(raw);
      final List<List<String>> result = new ArrayList<>();
      sentences.forEach(it -> result.add(tokenize(it)));
      return result;
   }

   public static List<String> tokenize(String sentence) {
      logger.trace("word tokenizing for " + sentence);
      return wordTokenizer.tokenize(sentence);
   }
}
