/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.TaggedWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class POSTagger {

  private static final Logger logger = LoggerFactory.getLogger(POSTagger.class);
  static ir.ac.iust.nlp.jhazm.POSTagger tagger;

  static {
    try {
      logger.info("creating pos tagger class of jhazm");
      tagger = new ir.ac.iust.nlp.jhazm.POSTagger();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<List<TaggedWord>> tagRaw(String raw) {
    final List<List<String>> sentences = WordTokenizer.tokenizeRaw(raw);
    return tagger.batchTags(sentences);
  }

  public static List<TaggedWord> tag(List<String> sentence) {
    logger.trace("pos tagging for " + sentence);
    return tagger.batchTag(sentence);
  }

  static void conllTaggedWords(List<List<TaggedWord>> taggedWords, Path path)
          throws IOException {
    final List<String> lines = new ArrayList<>();
    for (List<TaggedWord> taggedWordsOfSentence : taggedWords) {
      for (TaggedWord aTaggedWordsOfSentence : taggedWordsOfSentence)
        lines.add(aTaggedWordsOfSentence.word()
                + '\t' + aTaggedWordsOfSentence.tag());
      lines.add("");
    }
    Files.write(path, lines);
  }
}
