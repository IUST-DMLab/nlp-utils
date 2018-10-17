package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.ner.NerTaggedWord;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.persian.HistoryRecommender;

public class NamedEntityRecognizer {

  private static final Logger logger = LoggerFactory.getLogger(NamedEntityRecognizer.class);
  private static AbstractSequenceClassifier<CoreLabel> classifier;
  private static AbstractSequenceClassifier<CoreLabel> classifierPhase2;

  static {
    try {
      logger.info("loading NER classifiers");
      classifier = CRFClassifier.getClassifier(
              ConfigReader.INSTANCE.getString("ner.phase1.model", "ner-model.ser.gz"));
      classifierPhase2 = CRFClassifier.getClassifier(
              ConfigReader.INSTANCE.getString("ner.phase2.model", "ner-model-ph2.ser.gz"));
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static List<List<NerTaggedWord>> nerText(String text, boolean twoPassRun)
          throws IOException, InterruptedException {
    List<List<TaggedWord>> taggedWords = POSTagger.tagRaw(text);
    return nerTaggedWords(taggedWords, twoPassRun);
  }

  public static List<NerTaggedWord> nerSentence(List<TaggedWord> sentenceWords, boolean twoPassRun)
          throws IOException {
    final List<List<TaggedWord>> taggedWords = new ArrayList<>();
    taggedWords.add(sentenceWords);
    List<List<NerTaggedWord>> nerTagged = nerTaggedWords(taggedWords, twoPassRun);
    return nerTagged.get(0);
  }

  private static List<CoreLabel> twToCoreLabels(List<TaggedWord> taggedWords) {
    final List<CoreLabel> result = new ArrayList<>();
    for (int i = 0; i < taggedWords.size(); i++) {
      TaggedWord word = taggedWords.get(i);
      final CoreLabel label = new CoreLabel();
      label.set(CoreAnnotations.TextAnnotation.class, word.word());
      label.set(CoreAnnotations.ValueAnnotation.class, word.word());
      label.set(CoreAnnotations.PartOfSpeechAnnotation.class, word.tag());
      label.set(CoreAnnotations.PositionAnnotation.class, Integer.toString(i));
      label.set(CoreAnnotations.GoldAnswerAnnotation.class, null);
      result.add(label);
    }
    return result;
  }

  private static List<CoreLabel> clToCoreLabels(List<ResolvedEntityToken> tokens) {
    final List<CoreLabel> result = new ArrayList<>();
    for (int i = 0; i < tokens.size(); i++) {
      ResolvedEntityToken word = tokens.get(i);
      final CoreLabel label = new CoreLabel();
      label.set(CoreAnnotations.TextAnnotation.class, word.getWord());
      label.set(CoreAnnotations.ValueAnnotation.class, word.getWord());
      label.set(CoreAnnotations.PartOfSpeechAnnotation.class, word.getPos());
      label.set(CoreAnnotations.PositionAnnotation.class, Integer.toString(i));
      label.set(CoreAnnotations.GoldAnswerAnnotation.class, null);
      result.add(label);
    }
    return result;
  }

  private static void addNERs(List<List<ResolvedEntityToken>> tokens, List<List<CoreLabel>> nerOutput) {
    for (int i = 0; i < tokens.size(); i++) {
      final List<ResolvedEntityToken> ri = tokens.get(i);
      final List<CoreLabel> ci = nerOutput.get(i);
      for (int j = 0; j < ri.size(); j++) {
        ri.get(j).setNer(ci.get(j).get(CoreAnnotations.AnswerAnnotation.class));
      }
    }
  }

  public static void nerResolvedTokens(List<List<ResolvedEntityToken>> tokens, boolean twoPassRun) {
    final List<List<CoreLabel>> phase1Output = new ArrayList<>();
    for (List<ResolvedEntityToken> tw : tokens) {
      phase1Output.add(classifier.classify(clToCoreLabels(tw)));
    }

    if (twoPassRun) {
      try {
        HistoryRecommender.addRecommendations(phase1Output);
        final List<List<CoreLabel>> phase2Output = new ArrayList<>();
        for (List<CoreLabel> s : phase1Output) phase2Output.add(classifierPhase2.classify(s));
        addNERs(tokens, phase2Output);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else
      addNERs(tokens, phase1Output);
  }

  public static List<List<NerTaggedWord>> nerTaggedWords(List<List<TaggedWord>> taggedWords, boolean twoPassRun) {
    final List<List<CoreLabel>> phase1Output = new ArrayList<>();
    for (List<TaggedWord> tw : taggedWords) {
      phase1Output.add(classifier.classify(NamedEntityRecognizer.twToCoreLabels(tw)));
    }

    if (twoPassRun) {
      try {
        HistoryRecommender.addRecommendations(phase1Output);
        final List<List<CoreLabel>> phase2Output = new ArrayList<>();
        for (List<CoreLabel> s : phase1Output) phase2Output.add(classifierPhase2.classify(s));
        return getAsTaggedWord(phase2Output);
      } catch (IOException e) {
        return null;
      }
    } else
      return getAsTaggedWord(phase1Output);
  }

  public static List<List<NerTaggedWord>> getAsTaggedWord(List<List<CoreLabel>> phase2Output) {
    final List<List<NerTaggedWord>> result = new ArrayList<>();
    for (final List<CoreLabel> sentence : phase2Output) {
      List<NerTaggedWord> sentenceResult = new ArrayList<>();
      for (final CoreLabel word : sentence) {
        final String posTag = word.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        final String tag = word.get(CoreAnnotations.AnswerAnnotation.class);
        sentenceResult.add(new NerTaggedWord(word.word(), posTag, tag));
      }
      result.add(sentenceResult);
    }
    return result;
  }
}
