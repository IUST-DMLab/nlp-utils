/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.extractor.DependencyInformation;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class SimpleConstituencyParser {

  private static final Logger logger = LoggerFactory.getLogger(DependencyParser.class);

  public static List<List<ResolvedEntityToken>> constituency(String text) {
    final List<List<TaggedWord>> sentences = POSTagger.tagRaw(text);
    return constituencySentences(sentences);
  }

  @SuppressWarnings("WeakerAccess")
  public static List<List<ResolvedEntityToken>> constituencySentences(List<List<TaggedWord>> sentences) {
    final List<List<ResolvedEntityToken>> result = new ArrayList<>();
    for (List<TaggedWord> sentence : sentences)
      result.add(constituency(sentence));
    return result;
  }

  public static void addConstituencyParseSentences(List<List<ResolvedEntityToken>> sentences) {
    for (List<ResolvedEntityToken> sentence : sentences)
      addConstituencyParse(sentence);
  }

  @SuppressWarnings("WeakerAccess")
  public static List<ResolvedEntityToken> constituency(List<TaggedWord> sentence) {
    logger.info("check constituency for sentence " + sentence);
    final ConcurrentDependencyGraph parseTree = DependencyParser.parse(sentence);
    assert parseTree != null;
    final List<ResolvedEntityToken> result = new ArrayList<>();
    for (int i = 0; i < sentence.size(); i++) {
      TaggedWord word = sentence.get(i);
      final ResolvedEntityToken token = new ResolvedEntityToken();
      token.setWord(word.word());
      token.setPos(word.tag());
      token.setDep(new DependencyInformation(parseTree.getDependencyNode(i + 1)));
      result.add(token);
    }
    addConstituencyParse(result);
    return result;
  }

  public static void addConstituencyParse(List<ResolvedEntityToken> tokens) {
    for (ResolvedEntityToken token : tokens)
      token.setPhraseMates(new HashSet<>());
    for (int i = 0; i < tokens.size(); i++) {
      final ResolvedEntityToken token = tokens.get(i);
      if (token.getPhraseMates() == null) token.setPhraseMates(new HashSet<>());
      // HEADS In Dependency Parser starts with 1
      boolean linkedToNext = (token.getDep() != null && token.getDep().getHead() == i + 2) ||
          ((i < tokens.size() - 1) && (tokens.get(i + 1).getDep() != null)
              && (tokens.get(i + 1).getDep().getHead() == i + 1));
      if (linkedToNext) {
        token.getPhraseMates().add(i + 1);
        tokens.get(i + 1).getPhraseMates().add(i);
      }
    }
  }

  public static String tokensToString(List<ResolvedEntityToken> tokens) {
    final StringBuilder builder = new StringBuilder();
    builder.append('[');
    for (int i = 0; i < tokens.size(); i++) {
      ResolvedEntityToken token = tokens.get(i);
      builder.append(token.getWord());
      if (token.getPhraseMates().contains(i + 1)) builder.append(' ');
      else builder.append("] [");
    }
    builder.append('[');
    return builder.toString();
  }

  public static String sentencesToString(List<List<ResolvedEntityToken>> sentences) {
    final StringBuilder builder = new StringBuilder();
    for (List<ResolvedEntityToken> sentence : sentences)
      builder.append(tokensToString(sentence)).append('\n');
    if (builder.length() > 0) builder.setLength(builder.length() - 1);
    return builder.toString();
  }
}
