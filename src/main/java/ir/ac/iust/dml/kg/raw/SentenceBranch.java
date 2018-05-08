/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

public class SentenceBranch {

  public static String summarize(String text) {
    return text.replaceAll("\\s*" + "[\\،\\.و]*" + "\\s+" +
            "(آنگاه|اما|علاوه بر این|در واقع|در مقابل|از آن سوی|از سوی دیگر|ولی|به هر حال|بنابراین)" + "\\s+"
        , ". ").replaceAll("\\s*" + "[\\،\\.]+" + "\\s+" +
            "(که|البته|و)" + "\\s+"
        , ". ");
  }
}
