/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.pipeline.Annotation;

import java.util.List;

public class main {
  public static void main(String[] args) {
    String inputText=args[0];
    Annotation annotation=new Annotation(inputText);
    new ir.ac.iust.dml.kg.raw.TextProcess().preProcess(annotation);
    List<CorefChain> corefChains=new ReferenceFinder().annotateCoreference(annotation);
    return;
  }
}
