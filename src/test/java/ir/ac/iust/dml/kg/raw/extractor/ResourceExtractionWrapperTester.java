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
    assert result.size() == 12;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags).get(0);
    assert result.size() == 7;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.Ambiguities).get(0);
    assert result.size() == 7;
    for (MatchedResource r : result) {
      assert r.getAmbiguities().isEmpty();
    }
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.NotMatchedLabels).get(0);
    assert result.size() == 3;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.EmptyClassTree).get(0);
    assert result.size() == 3;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags,
        FilterType.NotNullDisambiguatedFrom).get(0);
    assert result.size() == 3;
    result = wrapper.extract(sample, true, FilterType.CommonPosTags).get(0);
    assert result.size() == 4;
    result = wrapper.extract(sample, false, FilterType.CommonPosTags, FilterType.Properties).get(0);
    assert result.size() == 2;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages).get(0);
    assert result.size() == 1;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages, FilterType.Things).get(0);
    assert result.size() == 1;
    result = wrapper.extract(sample, false,
        FilterType.CommonPosTags, FilterType.Properties, FilterType.Villages,
        FilterType.Things, FilterType.AnyResources).get(0);
    assert result.size() == 0;
  }
}
