package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.pipeline.Annotation;

import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 3/14/17 10:02 PM
 */
public class main {
    public static void main(String[] args) {
        String inputText=args[0];
        Annotation annotation=new Annotation(inputText);
       new ir.ac.iust.dml.kg.raw.TextProcess().preProcess(annotation);
        List<CorefChain> corefChains=new ReferenceFinder().annotateCoreference(annotation);
        return;
    }
}
