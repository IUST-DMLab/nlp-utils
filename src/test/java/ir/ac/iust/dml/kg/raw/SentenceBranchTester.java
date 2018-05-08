/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import org.junit.Test;

import java.util.Objects;

public class SentenceBranchTester {
  @Test
  public void summarize() {
    String input = "من تو را دوست دارم، اما تو مرا دوست نداری.";
    String expected = "من تو را دوست دارم. تو مرا دوست نداری.";
    assert Objects.equals(expected, SentenceBranch.summarize(input));

    input = "من تو را دوست دارم، از سوی دیگر تو مرا دوست نداری.";
    expected = "من تو را دوست دارم. تو مرا دوست نداری.";
    assert Objects.equals(expected, SentenceBranch.summarize(input));

    input = "من تو را دوست دارم، که تو مرا دوست نداری.";
    expected = "من تو را دوست دارم. تو مرا دوست نداری.";
    assert Objects.equals(expected, SentenceBranch.summarize(input));

    input = "در سال\u200Cهای بعد از ۶۱۷ قمری یعنی اواسط دهه ۱۲۲۰ میلادی بهاءالدین ولد و خانواده\u200Cاش که جلال\u200C" +
        "الدین محمد بلخی (مولوی) نیز در آن بود به آناتولی مرکزی، روم رسیدند.";
    expected = "در سال\u200Cهای بعد از ۶۱۷ قمری یعنی اواسط دهه ۱۲۲۰ میلادی بهاءالدین ولد و خانواده\u200Cاش که جلال\u200C" +
        "الدین محمد بلخی (مولوی) نیز در آن بود به آناتولی مرکزی، روم رسیدند.";
    assert Objects.equals(expected, SentenceBranch.summarize(input));
  }
}
