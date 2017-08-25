package ir.ac.iust.dml.kg.raw.extractor;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

public class EnhancedEntityExtractorTester {

  @Test
  public void test() {
    final EnhancedEntityExtractor extractor = new EnhancedEntityExtractor();
    final String sample = "من مجید هستم نه علی لاریجانی که نویسنده است و در روستای ابیانه زاده شده است.";
    final List<List<ResolvedEntityToken>> result = extractor.extract(sample);
    extractor.dependencyParse(result);
    for (List<ResolvedEntityToken> sentence : result)
      for (ResolvedEntityToken token : sentence)
        assert token.getDep() != null;
    final Path file = ConfigReader.INSTANCE.getPath("test.mode.export.triples", "~/raw/test.json");
    final boolean exported = EnhancedEntityExtractor.exportToFile(file, result);
    assert exported;

    final List<List<ResolvedEntityToken>> imported = EnhancedEntityExtractor.importFromFile(file);
    assert imported != null;
    assert imported.size() == result.size();

    System.out.println(result.size());
  }
}
