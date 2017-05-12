package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ir.ac.iust.dml.kg.raw.TextProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 1/26/17 12:00 PM
 */
public class ReferenceFinder {

    private void main(String[] args) {

    }

    public List<CorefChain> extractCorefChains(String inputText) {
        Annotation annotation = new Annotation(inputText);

        TextProcess tp = new TextProcess();
        tp.preProcess(annotation);
        tp.annotateNamedEntity(annotation);

        ReferenceFinder rfinder = new ReferenceFinder();
        List<ir.ac.iust.dml.kg.raw.coreference.CorefChain> corefChains = rfinder.annotateCoreference(annotation);
        return corefChains;
    }

    public List<CorefChain> annotateCoreference(Annotation annotation) {
        List<CorefChain> finalCorefChains = new ArrayList<CorefChain>();
        QuotationExtractor quotationExtractor = new QuotationExtractor();
        List<QuotationBound> quotationBounds = quotationExtractor.applyQuotationRules(annotation);
        int index = 0;
        List<Mention> allQuotationMentions = new ArrayList<Mention>();
        List<ReferenceEntity> allQuotationReference = new ArrayList<ReferenceEntity>();
        List<Mention> allQuotationTellerMentions = new ArrayList<Mention>();
        List<ReferenceEntity> allQuotationTellerReference = new ArrayList<ReferenceEntity>();
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

    public List<CorefChain> extractChainsFromRawText(Annotation annotation) {
        List<CorefChain> corefChains = new ArrayList<CorefChain>();
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        int sentenceIndex = 0;
        for (int i = 0; i < sentences.size(); i++) {

            List<CoreLabel> coreLabels = sentences.get(i).get(CoreAnnotations.TokensAnnotation.class);
            List<Mention> mentions = new CorefUtility().getMentions(coreLabels, sentenceIndex);
            for (Mention mention : mentions) {
                for (int j = i; j >= 0; j--) {
                    List<CoreLabel> sentenceCoreLabels = sentences.get(j).get(CoreAnnotations.TokensAnnotation.class);
                    List<ReferenceEntity> referenceEntities = new CorefUtility().getReferenceEntities(sentenceCoreLabels, j);
                    CorefChain corefChain = extractChainsFromSentence(referenceEntities, mention);
                    if (corefChain.getMentions() != null) {
                        corefChains.add(corefChain);
                        break;
                    } else {
                        continue;
                    }
                }
            }
            sentenceIndex++;
        }
        return corefChains;
    }

    private CorefChain extractChainsFromSentence(List<ReferenceEntity> referenceEntities, Mention mention) {
        List<Mention> mentions = new ArrayList<Mention>();
        mentions.add(mention);
        CorefChain corefChain = new CorefChain();
        for (ReferenceEntity referenceEntity : referenceEntities) {
            if (referenceEntity.getType().equals(mention.getType())) {
                corefChain.setMentions(mentions);
                corefChain.setReferenceEntity(referenceEntity);
            }
        }
        return corefChain;
    }

    private List<CorefChain> extractChainsFromQuotaionTellerBound(List<Mention> allMentionInQuotationTellerBound, List<ReferenceEntity> allReferencesInQuotationTellerBound) {
        List<CorefChain> quotationTellerChains = new ArrayList<CorefChain>();

        ListIterator li = allMentionInQuotationTellerBound.listIterator(allMentionInQuotationTellerBound.size());
        while (li.hasPrevious()) {
            Mention mention = (Mention) li.previous();
            int mentionIndex = mention.getIndex();
            List<ReferenceEntity> referenceCandidateEntities = getCandidateReferenceEntities(allReferencesInQuotationTellerBound, mention);
            if (referenceCandidateEntities.size() > 0) {
                CorefChain corefChain = new CorefChain();
                List<Mention> mentionList = new ArrayList<Mention>();
                mentionList.add(mention);
                corefChain.setReferenceEntity(referenceCandidateEntities.get(0));
                corefChain.setMentions(mentionList);
                quotationTellerChains.add(corefChain);
            }
        }
        return quotationTellerChains;
    }

    private List<ReferenceEntity> getCandidateReferenceEntities(List<ReferenceEntity> referenceEntities, Mention mention) {

        List<ReferenceEntity> referenceCandidate = new ArrayList<ReferenceEntity>();
        int mentionIndex = mention.getIndex();
        for (ReferenceEntity referenceEntity : referenceEntities) {
            int referenceIndex = referenceEntity.getIndex();
            if (referenceIndex < mentionIndex) {
                if (referenceEntity.getNumber() == mention.getNumber() && referenceEntity.getType() == mention.getType())
                    referenceCandidate.add(referenceEntity);
            }
        }
        return referenceEntities;
    }

}
