package ir.ac.iust.dml.kg.raw.extractor;

import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import org.junit.Test;

import java.util.List;

public class ResourceExtractionWrapperTester {

  @Test
  public void test() {
    final ResourceExtractionWrapper wrapper = new ResourceExtractionWrapper();
    final String sample = "من مجید هستم نه علی لاریجانی که نویسنده است و در روستای ابیانه زاده شده است.";
    List<MatchedResource> result = wrapper.extract(sample, false).get(0);
    assert result.size() == 15;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags).get(0);
    assert result.size() == 9;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.Ambiguities).get(0);
    assert result.size() == 2;
    for (MatchedResource r : result) {
      assert r.getAmbiguities().isEmpty();
    }
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.NotMatchedLabels).get(0);
    assert result.size() == 9;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.EmptyClassTree).get(0);
    assert result.size() == 8;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags,
        FilterType.NotNullDisambiguatedFrom).get(0);
    assert result.size() == 9;
    result = wrapper.extract(sample, true, FilterType.CommonPosTags).get(0);
    assert result.size() == 5;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.Properties).get(0);
    assert result.size() == 8;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages).get(0);
    assert result.size() == 8;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages, FilterType.Things).get(0);
    assert result.size() == 2;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages,
        FilterType.Things, FilterType.AnyResources).get(0);
    assert result.size() == 0;
  }
}
