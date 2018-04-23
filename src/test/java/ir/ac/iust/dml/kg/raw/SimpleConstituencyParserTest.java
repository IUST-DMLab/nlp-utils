/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import org.junit.Test;

import java.util.List;

public class SimpleConstituencyParserTest {

  private EnhancedEntityExtractor enhancedEntityExtractor;

  @Test
  public void constituency1() {
    String input = "علی دایی در سال ١٣٣٢ در شهر تهران و در خانواده ای سنتی متولد شد.";
    List<List<ResolvedEntityToken>> result = SimpleConstituencyParser.constituency(input);
    System.out.println(SimpleConstituencyParser.sentencesToString(result));
    input = "من و خلاش داریم از اول صبح تا حالا با هم یک چیز را راه می‌اندازیم.";
    result = SimpleConstituencyParser.constituency(input);
    System.out.println(SimpleConstituencyParser.sentencesToString(result));
  }

  //  @Test
  public void constituency2() {
    String input = "علی دایی در سال ١٣٣٢ در شهر تهران و در خانواده ای سنتی متولد شد.";
    if (enhancedEntityExtractor == null) enhancedEntityExtractor = new EnhancedEntityExtractor();
    final List<List<ResolvedEntityToken>> resolved = enhancedEntityExtractor.extract(input);
    enhancedEntityExtractor.disambiguateByContext(resolved, 3, 0.0001f);
    enhancedEntityExtractor.resolveByName(resolved);
    enhancedEntityExtractor.resolvePronouns(resolved);
    DependencyParser.addDependencyParseSentences(resolved);
    SimpleConstituencyParser.addConstituencyParseSentences(resolved);
    System.out.println(SimpleConstituencyParser.sentencesToString(resolved));
  }
}
