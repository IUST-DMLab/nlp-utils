package ir.ac.iust.dml.kg.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SentenceTokenizer {

   private static final Logger logger = LoggerFactory.getLogger(SentenceTokenizer.class);
   private static ir.ac.iust.nlp.jhazm.SentenceTokenizer sentenceTokenizer = new ir.ac.iust.nlp.jhazm.SentenceTokenizer();

   public static List<String> tokenizeRaw(String text) {
      return tokenize(Normalizer.normalize(text));
   }

   public static List<String> tokenize(String text) {
      logger.trace("tokenize to sentences: " + text);
      return sentenceTokenizer.tokenize(text);
   }
}
