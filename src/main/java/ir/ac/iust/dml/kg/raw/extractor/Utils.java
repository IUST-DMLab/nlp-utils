package ir.ac.iust.dml.kg.raw.extractor;

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
}
