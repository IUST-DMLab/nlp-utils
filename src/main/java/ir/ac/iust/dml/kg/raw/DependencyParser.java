/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.extractor.DependencyInformation;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ir.ac.iust.dml.kg.raw.POSTagger.tagger;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DependencyParser {

  private static final Logger logger = LoggerFactory.getLogger(DependencyParser.class);
  private static ir.ac.iust.nlp.jhazm.DependencyParser parser;

  static {
    try {
      logger.info("creating dependency parser class of jhazm");
      ir.ac.iust.nlp.jhazm.Lemmatizer lemmatizer = new ir.ac.iust.nlp.jhazm.Lemmatizer();
      parser = new ir.ac.iust.nlp.jhazm.DependencyParser(tagger, lemmatizer, "resources/langModel.mco");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<ConcurrentDependencyGraph> parseRaw(String raw) {
    final List<List<TaggedWord>> sentences = POSTagger.tagRaw(raw);
    return parseSentences(sentences);
  }

  public static ConcurrentDependencyGraph parse(List<TaggedWord> sentence) {
    logger.trace("dependency parsing for " + sentence.stream().map(Object::toString).collect(Collectors.joining(", ")));
    try {
      return parser.rawParse(sentence);
    } catch (IOException | MaltChainedException e) {
      logger.trace("error in dependency parse", e);
    }
    return null;
  }

  public static void addDependencyParse(List<ResolvedEntityToken> sentenceTokens, boolean shrunk) {
    try {
      final List<TaggedWord> taggedWords;
      if (shrunk) {
        List<String> text = new ArrayList<>();
        for (ResolvedEntityToken token : sentenceTokens) {
          if (token.getShrunkWords() != null) text.add("موجودیتاسمی");
          else text.add(token.getWord());
        }
        taggedWords = POSTagger.tag(text);
      } else {
        taggedWords = new ArrayList<>();
        for (ResolvedEntityToken token : sentenceTokens)
          taggedWords.add(new TaggedWord(token.getWord(), token.getPos()));
      }
      final ConcurrentDependencyGraph parseTree = parser.rawParse(taggedWords);
      for (int i = 0; i < sentenceTokens.size(); i++) {
        final ResolvedEntityToken token = sentenceTokens.get(i);
        if (shrunk) token.setPos(taggedWords.get(i).tag());
        token.setDep(new DependencyInformation(parseTree.getDependencyNode(i + 1)));
      }
    } catch (IOException | MaltChainedException e) {
      logger.trace("error in dependency parse", e);
    }
  }

  public static List<ConcurrentDependencyGraph> parseSentences(List<List<TaggedWord>> sentences) {
    final List<ConcurrentDependencyGraph> result = new ArrayList<>();
    sentences.forEach(it -> result.add(parse(it)));
    return result;
  }

  public static void addDependencyParseSentences(List<List<ResolvedEntityToken>> sentences, boolean shrunk) {
    for (List<ResolvedEntityToken> sentence : sentences) addDependencyParse(sentence, shrunk);
  }
}
