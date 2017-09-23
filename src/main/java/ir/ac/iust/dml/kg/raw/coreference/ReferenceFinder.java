/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ir.ac.iust.dml.kg.raw.TextProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReferenceFinder {

  public List<CorefChain> extractCorefChains(String inputText) {
    Annotation annotation = new Annotation(inputText);

    TextProcess tp = new TextProcess();
    tp.preProcess(annotation);
    tp.annotateNamedEntity(annotation);

    ReferenceFinder rfinder = new ReferenceFinder();
    return rfinder.annotateCoreference(annotation);
  }

  public List<CorefChain> annotateCoreference(Annotation annotation) {
    List<CorefChain> finalCorefChains;
    QuotationExtractor quotationExtractor = new QuotationExtractor();
    List<QuotationBound> quotationBounds = quotationExtractor.applyQuotationRules(annotation);
    int index = 0;
    List<Mention> allQuotationMentions = new ArrayList<>();
    List<ReferenceEntity> allQuotationReference = new ArrayList<>();
    List<Mention> allQuotationTellerMentions = new ArrayList<>();
    List<ReferenceEntity> allQuotationTellerReference = new ArrayList<>();
    if (quotationBounds.size() > 0) {
      for (QuotationBound qBound : quotationBounds)

      {
        index++;
        List<Mention> quotationTellerMentions = CorefUtility.getMentions(qBound.getTellerBoundCoreLabels(), index);
        allQuotationTellerMentions.addAll(quotationTellerMentions);
        List<ReferenceEntity> quotationTellerReferences = CorefUtility.getReferenceEntities(qBound.getTellerBoundCoreLabels(), index);
        allQuotationTellerReference.addAll(quotationTellerReferences);

        List<Mention> quotationMentions = CorefUtility.getMentions(qBound.getQuotationBoundCoreLabels(), index);
        allQuotationMentions.addAll(quotationMentions);
        List<ReferenceEntity> quotationReferences = CorefUtility.getReferenceEntities(qBound.getQuotationBoundCoreLabels(), index);
        allQuotationReference.addAll(quotationReferences);
      }

      finalCorefChains = extractChainsFromQuotaionTellerBound(allQuotationTellerMentions, allQuotationTellerReference);

      finalCorefChains.addAll(extractChainsFromQuotaionTellerBound(allQuotationMentions, allQuotationReference));
    } else {
      finalCorefChains = extractChainsFromRawText(annotation);
    }
    return finalCorefChains;
  }

  public String getAnnotationTextAfterCoref(String inputText) {
    Annotation annotation = new Annotation(inputText);

    TextProcess tp = new TextProcess();
    tp.preProcess(annotation);
    tp.annotateNamedEntity(annotation);

    extractChainsFromRawText(annotation);

    StringBuilder outputText = new StringBuilder();
    for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
      for (CoreLabel coreLabel : sentence.get(CoreAnnotations.TokensAnnotation.class))
        outputText.append(" ").append(coreLabel.get(CoreAnnotations.TextAnnotation.class));
      outputText.append(" ");
    }
    return outputText.toString();
  }

  public List<CorefChain> extractChainsFromRawText(Annotation annotation) {
    List<CorefChain> corefChains = new ArrayList<>();
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    int sentenceIndex = 0;
    for (int i = 0; i < sentences.size(); i++) {

      List<CoreLabel> coreLabels = sentences.get(i).get(CoreAnnotations.TokensAnnotation.class);
      List<Mention> mentions = CorefUtility.getMentions(coreLabels, sentenceIndex);
      for (Mention mention : mentions) {
        for (int j = i; j >= 0; j--) {
          List<CoreLabel> sentenceCoreLabels = sentences.get(j).get(CoreAnnotations.TokensAnnotation.class);
          List<ReferenceEntity> referenceEntities = CorefUtility.getReferenceEntities(sentenceCoreLabels, j);
          CorefChain corefChain = extractChainsFromSentence(referenceEntities, mention);
          if (corefChain.getMentions() != null) {
            changeMentionText(corefChain);
            corefChains.add(corefChain);
            break;
          }
        }
      }
      sentenceIndex++;
    }
    return corefChains;
  }

  private void changeMentionText(CorefChain corefChain) {
    //Mention mention=corefChain.getMentions().get(0);
    corefChain.getMentions().get(0).getMentionCoreLabel().set(CoreAnnotations.TextAnnotation.class, corefChain.getReferenceEntity().toString());
  }

  private CorefChain extractChainsFromSentence(List<ReferenceEntity> referenceEntities, Mention mention) {
    List<Mention> mentions = new ArrayList<>();
    mentions.add(mention);
    CorefChain corefChain = new CorefChain();
    int mentionIndex = mention.getMentionCoreLabel().get(CoreAnnotations.IndexAnnotation.class);

    for (ReferenceEntity referenceEntity : referenceEntities) {
      int referenceIndex = referenceEntity.getEntityTokens().get(0).get(CoreAnnotations.IndexAnnotation.class);
      if (referenceEntity.getType().equals(mention.getType())) {
        if (referenceIndex < mentionIndex)
          corefChain.setMentions(mentions);
        corefChain.setReferenceEntity(referenceEntity);
      }
    }
    return corefChain;
  }

  private List<CorefChain> extractChainsFromQuotaionTellerBound(List<Mention> allMentionInQuotationTellerBound, List<ReferenceEntity> allReferencesInQuotationTellerBound) {
    List<CorefChain> quotationTellerChains = new ArrayList<>();

    ListIterator li = allMentionInQuotationTellerBound.listIterator(allMentionInQuotationTellerBound.size());
    while (li.hasPrevious()) {
      Mention mention = (Mention) li.previous();
      List<ReferenceEntity> referenceCandidateEntities = getCandidateReferenceEntities(allReferencesInQuotationTellerBound, mention);
      if (referenceCandidateEntities.size() > 0) {
        CorefChain corefChain = new CorefChain();
        List<Mention> mentionList = new ArrayList<>();
        mentionList.add(mention);
        corefChain.setReferenceEntity(referenceCandidateEntities.get(0));
        corefChain.setMentions(mentionList);
        quotationTellerChains.add(corefChain);
      }
    }
    return quotationTellerChains;
  }

  private List<ReferenceEntity> getCandidateReferenceEntities(List<ReferenceEntity> referenceEntities, Mention amention) {
    return referenceEntities;
//        List<ReferenceEntity> referenceCandidate = new ArrayList<>();
//        int mentionIndex = mention.getIndex();
//        for (ReferenceEntity referenceEntity : referenceEntities) {
//            int referenceIndex = referenceEntity.getIndex();
//            if (referenceIndex < mentionIndex) {
//                if (referenceEntity.getNumber() == mention.getNumber() && referenceEntity.getType() == mention.getType())
//                    referenceCandidate.add(referenceEntity);
//            }
//        }
//        return referenceEntities;
  }

}
