package ir.ac.iust.dml.kg.raw.extractor;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Utils {

  /**
   * check whether a tag is good for similarity calculation or not. in this way we don't count
   * stop words.
   *
   * @param tag POS tag of word
   * @return true if it is a bad tag
   */
  static boolean isBadTag(String tag) {
    return tag.equals("P") || tag.equals("Pe") || tag.equals("POSTP") ||
        tag.equals("DET") || tag.equals("NUM") || tag.equals("PUNC") ||
        tag.equals("CONJ") || tag.equals("PRO") || tag.equals("ADV") || tag.equals("V");
  }

  private static final HashSet<String> frequentWords = new HashSet<>();

  static {
    final Path path = ConfigReader.INSTANCE.getPath("raw.enhanced.extractor.frequent.words",
            "~/.pkg/frequent_words.txt").toAbsolutePath();
    try {
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
        try (InputStream in = Utils.class.getClassLoader().getResourceAsStream("frequent_words.txt")) {
          Files.copy(in, path);
        }
      }
      final List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
      frequentWords.addAll(lines);
    } catch (Throwable th) {
      th.printStackTrace();
    }
  }


  static boolean isFrequentWord(String word) {
    return frequentWords.contains(word);
  }
}
