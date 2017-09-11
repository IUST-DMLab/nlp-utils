package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import ir.ac.iust.dml.kg.raw.coreference.CorefUtility;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 2/4/17 10:35 PM
 */
public class TextProcess {

    private WordTokenizer wordTokenizer;
    private POSTagger posTagger;
    private Lemmatizer lemmatizer;
    private Normalizer normalizer;
    private SentenceTokenizer sentenceTokenizer;
    private DependencyParser dependencyParser;
    private List<String> entityList;

    public TextProcess() {
        entityList = new CorefUtility().readListedFile(TextProcess.class, "/personLexicon.txt");
        // entityList = new ir.ac.iust.dml.kg.raw.coreference.CorefUtility().readLines(this.getClass().getResource("/personLexicon.txt").getPath().substring(1));
        normalizer = new Normalizer();
        // String wordsFilePath = this.getClass().getResource("/data/words.dat").getPath().substring(1);
        //String verbsFilePath = this.getClass().getResource("/data/verbs.dat").getPath().substring(1);
        // wordTokenizer = new WordTokenizer();
        // posTagger = new POSTagger(this.getClass().getResource("/models/persian.tagger").getPath().substring(1));
        lemmatizer = new Lemmatizer();
        //   dependencyParser = new DependencyParser(posTagger, lemmatizer, this.getClass().getResource("/models/langModel.mco").getPath().substring(1));


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


    public void preProcess(Annotation annotation) {
        annotation.set(CoreAnnotations.OriginalTextAnnotation.class, annotation.toString());
        String normalizeText = Normalizer.normalize(annotation.toString());
        annotation.set(CoreAnnotations.TextAnnotation.class, normalizeText);
        String[] paragraphs = normalizeText.split("\n");
        List<CoreMap> paragraphsforAnnotation = new ArrayList<CoreMap>();
        List<CoreLabel> allCoreLabels = new ArrayList<CoreLabel>();
        List<CoreMap> allCoreMaps = new ArrayList<CoreMap>();
        int tokenIndex = 0;
        for (String paragraph : paragraphs) {
            CoreMap newParagraph = new ArrayCoreMap();
            List<String> sentences = SentenceTokenizer.SentrenceSplitter(paragraph);
            List<CoreMap> coreMaps = new ArrayList<CoreMap>();

            List<CoreLabel> coreLabels = new ArrayList<CoreLabel>();
            for (String sentence : sentences) {
                List<String> words = WordTokenizer.tokenize(sentence);
                List<TaggedWord> tags = POSTagger.tag(words);
                List<CoreLabel> sentenceCoreLabel = new ArrayList<CoreLabel>();
                CoreMap sentenceCoreMap = new ArrayCoreMap();
                sentenceCoreMap.set(CoreAnnotations.TextAnnotation.class, sentence);
                for (int i = 0; i < words.size(); i++) {
                    CoreLabel coreLabel = new CoreLabel();
                    coreLabel.setWord(words.get(i));
                    coreLabel.setTag(tags.get(i).tag());
                    String lemma = null;
                    try {
                        lemma = lemmatizer.lemmatize(words.get(i), tags.get(i).tag());
                    } catch (NoClassDefFoundError ex) {
                        lemma = words.get(i);
                    }


                    coreLabel.setLemma(lemma);
                    coreLabels.add(coreLabel);
                    coreLabel.set(CoreAnnotations.IndexAnnotation.class, tokenIndex);
                    tokenIndex++;
                    sentenceCoreLabel.add(coreLabel);
                }
                sentenceCoreMap.set(CoreAnnotations.TokensAnnotation.class, sentenceCoreLabel);
                coreMaps.add(sentenceCoreMap);


            }
            allCoreLabels.addAll(coreLabels);
            allCoreMaps.addAll(coreMaps);
            newParagraph.set(CoreAnnotations.TokensAnnotation.class, coreLabels);
            newParagraph.set(CoreAnnotations.SentencesAnnotation.class, coreMaps);
            paragraphsforAnnotation.add(newParagraph);
        }
        annotation.set(CoreAnnotations.ParagraphsAnnotation.class, paragraphsforAnnotation);
        annotation.set(CoreAnnotations.TokensAnnotation.class, allCoreLabels);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, allCoreMaps);

    }

    public void paragraphSplitter(Annotation annotation) {

        List<CoreLabel> paragraphCoreLabels = new ArrayList<CoreLabel>();
        List<CoreMap> paragraphs = new ArrayList<CoreMap>();
        for (CoreLabel coreLabel : annotation.get(CoreAnnotations.TokensAnnotation.class)) {
            paragraphCoreLabels.add(coreLabel);
            if (coreLabel.word().equals("\n")) {
                CoreMap coreMap = new ArrayCoreMap();
                coreMap.set(CoreAnnotations.TokensAnnotation.class, paragraphCoreLabels);
                paragraphs.add(coreMap);
                paragraphCoreLabels.clear();
            }
        }
        annotation.set(CoreAnnotations.ParagraphsAnnotation.class, paragraphs);
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
        int windowSize = 6;//average entity length is about  
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

            if ((startOfWindow + windowSizeIndex <= tokens.size()) && neList.contains(candidateString.replace("\uFEFF", ""))) {
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


    public Annotation getAnnotationFromEntityTokens(List<List<ResolvedEntityToken>> tokens) {
        Annotation annotation = new Annotation();
        int tokenIndex = 0;
        List<CoreLabel> allCoreLabels = new ArrayList<CoreLabel>();
        List<CoreMap> allCoreMaps = new ArrayList<CoreMap>();

        for (List<ResolvedEntityToken> resolvedEntityTokens : tokens) {
            CoreMap sentenceCoreMap = new ArrayCoreMap();
            List<CoreLabel> sentenceCoreLabels = new ArrayList<CoreLabel>();
            String senetceStr = "";
            for (ResolvedEntityToken resolvedEntityToken : resolvedEntityTokens) {
                CoreLabel coreLabel = new CoreLabel();
                senetceStr += resolvedEntityToken.getWord() + " ";
                coreLabel.setWord(resolvedEntityToken.getWord());
                coreLabel.setTag(resolvedEntityToken.getPos());
                coreLabel.setOriginalText(resolvedEntityToken.getWord());
                coreLabel.setWord(resolvedEntityToken.getWord());
                coreLabel.set(CoreAnnotations.IndexAnnotation.class, tokenIndex);

                tokenIndex++;

            }


            sentenceCoreMap.set(CoreAnnotations.TextAnnotation.class, senetceStr.trim());
            sentenceCoreMap.set(CoreAnnotations.TokensAnnotation.class, sentenceCoreLabels);
            allCoreLabels.addAll(sentenceCoreLabels);
            allCoreMaps.add(sentenceCoreMap);

        }

        annotation.set(CoreAnnotations.TokensAnnotation.class, allCoreLabels);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, allCoreMaps);






        return annotation;
    }
}
