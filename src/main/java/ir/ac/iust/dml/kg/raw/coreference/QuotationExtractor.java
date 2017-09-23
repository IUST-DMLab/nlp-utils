/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.ArrayList;
import java.util.List;

public class QuotationExtractor {

  private static Configuration config;

  static {
    try {
      config = new PropertiesConfiguration("config.properties");
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  private List<String> rules;

  public QuotationExtractor() {

      /*  String rulePath = this.getClass().getResource("/quotationRules.txt").getPath().substring(1);
       System.out.println(rulePath);*/
    rules = new CorefUtility().readListedFile(QuotationExtractor.class, "/quotationRules.txt");
    // rules = new CorefUtility().readLines(rulePath);


  }

  public void annotateQuotation(Annotation annotation) {
    List<CoreLabel> annotatCoreLabels = new ArrayList<CoreLabel>();
    List<QuotationBound> quotationBounds = applyQuotationRules(annotation);

    // annotation.set(CoreAnnotations.QuotationAnnotation.class,quotationBounds);
  }


  public List<QuotationBound> applyQuotationRules(Annotation annotation) {
    Env environment = TokenSequencePattern.getNewEnv();
    String rule = rules.get(0);
    List<CoreMap> paragraphs = annotation.get(CoreAnnotations.ParagraphsAnnotation.class);
    TokenSequencePattern pattern = TokenSequencePattern.compile(environment, rule);
    List<QuotationBound> quotationBounds = new ArrayList<QuotationBound>();
    for (CoreMap paragraph : paragraphs) {
      List<CoreLabel> StanfordTokens = paragraph.get(CoreAnnotations.TokensAnnotation.class);
      TokenSequenceMatcher matcher = pattern.getMatcher(StanfordTokens);

      while (matcher.find()) {
        QuotationBound quotationBound = getQuotationBound(StanfordTokens, matcher);
        quotationBounds.add(quotationBound);
      }
    }

    return quotationBounds;
  }


  private QuotationBound getQuotationBound(List<CoreLabel> coreLabels, TokenSequenceMatcher matcher) {

    QuotationBound quotationBound = new QuotationBound();
    SequenceMatchResult.MatchedGroupInfo<edu.stanford.nlp.util.CoreMap> coreMaps = matcher.groupInfo("$quotation");
    List<edu.stanford.nlp.ling.CoreLabel> stanfordCoreLabel = (List<edu.stanford.nlp.ling.CoreLabel>) coreMaps.nodes;

    quotationBound.setQuotationBoundCoreLabels(stanfordCoreLabel);
    quotationBound.setQuotationString(matcher.group("$quotation"));

    coreMaps = matcher.groupInfo("$teller");
    stanfordCoreLabel = (List<edu.stanford.nlp.ling.CoreLabel>) coreMaps.nodes;
    quotationBound.setTellerBoundCoreLabels(stanfordCoreLabel);
    quotationBound.setTellerString(matcher.group("$teller"));

    return quotationBound;
  }

}
