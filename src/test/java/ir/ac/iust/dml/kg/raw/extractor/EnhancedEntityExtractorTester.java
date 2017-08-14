package ir.ac.iust.dml.kg.raw.extractor;

import org.junit.Test;

import java.util.List;

public class EnhancedEntityExtractorTester {

  @Test
  public void test() {
    final EnhancedEntityExtractor extractor = new EnhancedEntityExtractor();
    final String sample = "من مجید هستم نه علی لاریجانی که نویسنده است و در روستای ابیانه زاده شده است.";
    List<List<ResolvedEntityToken>> result = extractor.extract(sample);
    System.out.println(result.size());
  }
}
