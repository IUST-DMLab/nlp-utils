/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Normalizer {

  private static final Logger logger = LoggerFactory.getLogger(Normalizer.class);
  static ir.ac.iust.nlp.jhazm.Normalizer normalizer = new ir.ac.iust.nlp.jhazm.Normalizer();

  public static String normalize(String text) {
    text = text.replaceAll("[\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u0653\u0654\u0655]", "");
    logger.trace("normalizing text: " + text);
    return normalizer.run(text);
  }

  public static String removeBrackets(String text) {
    return text.replaceAll("\\s*[\\[\\({-][^\\.]*?[\\]\\)}-]\\s*", " ");
  }

  public void  annotate(Annotation annotation) {
    String annotationText=annotation.get(CoreAnnotations.TextAnnotation.class);
    annotation.set(CoreAnnotations.OriginalTextAnnotation.class,annotationText);
    annotation.set(CoreAnnotations.TextAnnotation.class,normalize(annotationText));
  }
}
