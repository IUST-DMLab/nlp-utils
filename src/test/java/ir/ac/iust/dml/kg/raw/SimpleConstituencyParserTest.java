/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw;

import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
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

  @Test
  public void constituency2() throws IOException {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sample_sentences.txt");
    StringWriter writer = new StringWriter();
    IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
    String input = writer.toString();
    List<List<ResolvedEntityToken>> result = SimpleConstituencyParser.constituency(input);
    System.out.println(SimpleConstituencyParser.sentencesToString(result));
  }

  @Test
  public void constituencyAndRelation() throws IOException {
//    String input = "حاجی میرزا نصرالله معروف به ملک\u200Cالمتکلمین یک دورهٔ کامل فلسفه را نزد آخوند ملا صالح فریدنی آموخت.";
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sample_sentences.txt");
    StringWriter writer = new StringWriter();
    IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
    String input = writer.toString();
    if (enhancedEntityExtractor == null) enhancedEntityExtractor = new EnhancedEntityExtractor();
    List<List<ResolvedEntityToken>> resolved = enhancedEntityExtractor.extract(input);
    enhancedEntityExtractor.disambiguateByContext(resolved, 3, 0.0001f);
    enhancedEntityExtractor.resolveByName(resolved);
    enhancedEntityExtractor.resolvePronouns(resolved);
    resolved = enhancedEntityExtractor.shrinkNameEntitiesSentences(resolved);
    DependencyParser.addDependencyParseSentences(resolved, true);
    SimpleConstituencyParser.addConstituencyParseSentences(resolved);
//    resolved = enhancedEntityExtractor.augmentNameEntities(resolved);
    System.out.println(SimpleConstituencyParser.sentencesToString(resolved));
  }
}
