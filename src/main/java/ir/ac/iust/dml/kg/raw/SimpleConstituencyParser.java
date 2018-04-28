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
import java.util.HashMap;
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
    // We could combine these three for loops with one complicated hard-to-learn loop
    // set phrase mate and dependency mates. default value for them is null.
    for (ResolvedEntityToken token : tokens) {
      token.setPhraseMates(new HashSet<>());
      token.setDependencyMates(new HashMap<>());
    }

    // Thinking to dependency links as bi-directional links.
    for (int i = 0; i < tokens.size(); i++) {
      final ResolvedEntityToken token = tokens.get(i);
      if (token.getDep().getHead() == 0 || token.getDep().getHead() > tokens.size() - 2) continue;
      token.getDependencyMates().put(token.getDep().getHead() - 1, token.getDep());
      tokens.get(token.getDep().getHead() - 1).getDependencyMates().put(i, token.getDep());
    }
//
//    final List<Integer> lastPhrase = new ArrayList<>();
//    for (int i = 0; i < tokens.size(); i++) {
//      final ResolvedEntityToken token = tokens.get(i);
//      // HEADS In Dependency Parser starts with 1
//      boolean belongsToLastPhrase = false;
//      for (int lastPhraseWord : lastPhrase)
//        if (token.getDependencyMates().containsKey(lastPhraseWord)) {
//          String dep = token.getDependencyMates().get(lastPhraseWord).getRelation();
//          if(!dep.equals("APP") && !token.getWord().equals("است")) {
//            belongsToLastPhrase = true;
//            break;
//          }
//        }
//      if (!belongsToLastPhrase) {
//        lastPhrase.clear();
//      } else {
//        tokens.get(i - 1).getPhraseMates().add(i);
//        token.getPhraseMates().add(i - 1);
//      }
//      lastPhrase.add(i);
//    }

    for (int i = 0; i < tokens.size(); i++) {
      final ResolvedEntityToken token = tokens.get(i);
      // HEADS In Dependency Parser starts with 1
      boolean linkedToNext = (token.getDep() != null && token.getDep().getHead() == i + 2) ||
          ((i < tokens.size() - 1) && (tokens.get(i + 1).getDep() != null)
              && (tokens.get(i + 1).getDep().getHead() == i + 1)) ||
          (token.getPos().equals("از") ||
              (token.getPos().equals("به")));
      if (linkedToNext && i < tokens.size() - 1) {
        token.getPhraseMates().add(i + 1);
        tokens.get(i + 1).getPhraseMates().add(i);
      }

      boolean linkedToThePrevious =
          (token.getDep() != null && token.getDep().getRelation().equals("MOZ")) ||
              (token.getDep() != null && token.getDep().getRelation().equals("NCONJ")) ||
              (token.getPhraseMates().isEmpty() && token.getWord().equals("را"));
      if (linkedToThePrevious && i > 1) {
        token.getPhraseMates().add(i - 1);
        tokens.get(i - 1).getPhraseMates().add(i);
      }
    }
  }

  public static String tokensToString(List<ResolvedEntityToken> tokens) {
    final StringBuilder builder = new StringBuilder();
    builder.append('[');
    for (int i = 0; i < tokens.size(); i++) {
      ResolvedEntityToken token = tokens.get(i);
      if (token.getShrunkWords() == null) builder.append(token.getWord());
      else {
        for (ResolvedEntityToken shrunkWord : token.getShrunkWords())
          builder.append(shrunkWord.getWord()).append(" ");
        builder.setLength(builder.length() - 1);
      }
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
