package ir.ac.iust.dml.kg.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Normalizer {

   private static final Logger logger = LoggerFactory.getLogger(Normalizer.class);
   static ir.ac.iust.nlp.jhazm.Normalizer normalizer = new ir.ac.iust.nlp.jhazm.Normalizer();

   public static String normalize(String text) {
      logger.trace("normalizing text: " + text);
      return normalizer.run(text);
   }
}
