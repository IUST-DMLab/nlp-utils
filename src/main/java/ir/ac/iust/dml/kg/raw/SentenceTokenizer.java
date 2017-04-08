package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
