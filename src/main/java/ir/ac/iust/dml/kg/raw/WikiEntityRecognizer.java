/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import ir.ac.iust.dml.kg.raw.coreference.CorefUtility;

import java.util.List;

public class WikiEntityRecognizer {
    private List<String> entityList;

    WikiEntityRecognizer() {

        entityList = new CorefUtility().readListedFile(TextProcess.class, "/personLexicon.txt");
    }

    public void annotateNamedEntity(Annotation annotation) {
        List<CoreLabel> coreLabels = annotation.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel coreLabel : coreLabels) {
            coreLabel.setNER("O");
        }
        checkWordInEntityList(coreLabels, entityList);
    }

    public void checkWordInEntityList(List<CoreLabel> tokens, List<String> neList) {
        int startOfWindow = 0;
        int windowSize = 6;//average entity length
        int windowSizeIndex = windowSize;
        String candidateString = "";

        while (startOfWindow < tokens.size()) {
            if (tokens.get(startOfWindow).word().matches("[\\n\\r\\t\\s\\u200C]+")) {
                startOfWindow++;
                continue;
            }
            if (windowSizeIndex > tokens.size())
                windowSizeIndex = tokens.size();
            candidateString = getCandidateString(tokens, startOfWindow, windowSizeIndex);

            if ((startOfWindow + windowSizeIndex <= tokens.size()) && neList.contains(candidateString)) {
                assignTagToTokens(tokens, startOfWindow, windowSizeIndex);
                // this scope is final scope and the candidate string is finded.
                startOfWindow += windowSizeIndex;
                windowSizeIndex = windowSize;
            } else if (windowSizeIndex > 1) {
                windowSizeIndex--;
            } else {
                startOfWindow += 1;
                windowSizeIndex = windowSize;
            }
        }
    }

    protected void assignTagToTokens(List<CoreLabel> tokens, int startOfWindow, int windowSizeIndex) {

        for (int j = startOfWindow + windowSizeIndex - 1; j > startOfWindow; j--) {
            tokens.get(j).set(CoreAnnotations.NamedEntityTagAnnotation.class, "I_PERS");
        }
        tokens.get(startOfWindow).setNER("B_PERS");
    }

    private String getCandidateString(List<CoreLabel> tokens, int startOfWindow, int windowSizeIndex) {
        String candidateString;
        candidateString = "";
        for (int i = startOfWindow; i < startOfWindow + windowSizeIndex; i++)
            if (i < tokens.size())
                candidateString += tokens.get(i).word() + " ";
        candidateString = candidateString.trim();
        return candidateString;
    }
}
