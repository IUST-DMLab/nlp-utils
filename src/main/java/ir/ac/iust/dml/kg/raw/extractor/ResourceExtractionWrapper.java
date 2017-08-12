package ir.ac.iust.dml.kg.raw.extractor;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.resource.extractor.client.ExtractorClient;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ResourceExtractionWrapper {

  private final ExtractorClient client;

  public ResourceExtractionWrapper(String serviceAddress) {
    this.client = new ExtractorClient(serviceAddress);
  }

  public ResourceExtractionWrapper() {
    this.client = new ExtractorClient("http://194.225.227.161:8094");
  }

  public List<List<MatchedResource>> extract(String rawText, boolean removeSubset, FilterType... filterTypes) {
    final List<List<TaggedWord>> allTags = POSTagger.tagRaw(rawText);
    final List<List<MatchedResource>> result = new ArrayList<>();
    for (List<TaggedWord> tags : allTags) result.add(extract(tags, removeSubset, filterTypes));
    return result;
  }

  public List<MatchedResource> extract(List<TaggedWord> taggedWords, boolean removeSubset, FilterType... filterTypes) {
    final String text = taggedWords.stream().map(Word::word).collect(Collectors.joining(" "));
    final List<MatchedResource> result = client.match(text, removeSubset);
    for (MatchedResource resource : result) if (resource.getEnd() >= taggedWords.size()) return new ArrayList<>();

    final List<MatchedResource> filteredResult = new ArrayList<>();
    final Set<FilterType> filterTypeSet = new HashSet<>(Arrays.asList(filterTypes));
    for (MatchedResource resource : result) {

      if (filterTypeSet.contains(FilterType.CommonPosTags)) {
        boolean mustNotBeRemoved = false;
        for (int i = resource.getStart(); i <= resource.getEnd(); i++)
          if (!isBadTagForMatchedResource(taggedWords.get(i))) mustNotBeRemoved = true;
        if (!mustNotBeRemoved) continue;
      }

      if (filterTypeSet.contains(FilterType.CommonPosTagsStrict)) {
        boolean mustBeRemoved = false;
        for (int i = resource.getStart(); i <= resource.getEnd(); i++)
          if (isBadTagForMatchedResource(taggedWords.get(i))) {
            mustBeRemoved = true;
            break;
          }
        if (!mustBeRemoved) continue;
      }

      if (mustFilter(resource.getResource(), filterTypes))
        resource.setResource(null);
      final List<Resource> filteredAmbiguities = new ArrayList<>();
      for (Resource ambiguity : resource.getAmbiguities())
        if (mustFilter(ambiguity, filterTypes)) filteredAmbiguities.add(ambiguity);
      resource.getAmbiguities().removeAll(filteredAmbiguities);

      if (resource.getResource() != null || !resource.getAmbiguities().isEmpty())
        filteredResult.add(resource);
    }
    return filteredResult;
  }

  private boolean mustFilter(Resource resource, FilterType... filterTypes) {
    for (FilterType filterType : filterTypes) {
      if (mustFilter(resource, filterType)) return true;
    }
    return false;
  }

  private boolean mustFilter(Resource resource, FilterType filterType) {
    if(resource == null) return true;
    switch (filterType) {
      case AnyResources:
        if (resource.getType() == null || resource.getType() == ResourceType.Resource) return true;
      case Villages:
        if (resource.getInstanceOf() != null && resource.getInstanceOf().contains("Village")) return true;
      case Properties:
        if (resource.getType() != null && resource.getType() == ResourceType.Property) return true;
      case Things:
        if (resource.getInstanceOf() == null || resource.getInstanceOf().contains("Thing")) return true;
    }
    return false;
  }

  private boolean isBadTagForMatchedResource(TaggedWord taggedWord) {
    final String tag = taggedWord.tag();
    return tag.equals("P") || tag.equals("CONJ") || tag.equals("V") || tag.equals("PRO") || tag.equals("ADV");
  }
}
