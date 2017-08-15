package ir.ac.iust.dml.kg.raw.extractor;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class EnhancedEntityExtractor {

  private final ResourceExtractionWrapper client;

  public EnhancedEntityExtractor(String serviceAddress) {
    this.client = new ResourceExtractionWrapper(serviceAddress);
  }

  public EnhancedEntityExtractor() {
    this.client = new ResourceExtractionWrapper("http://194.225.227.161:8094");
  }

  public List<List<ResolvedEntityToken>> extract(String rawText) {
    return extract(rawText, true, FilterType.CommonPosTags, FilterType.Properties);
  }

  public List<List<ResolvedEntityToken>> extract(String rawText, boolean removeSubset, FilterType... filterTypes) {
    final List<List<TaggedWord>> allTags = POSTagger.tagRaw(rawText);
    final List<List<ResolvedEntityToken>> result = new ArrayList<>();
    for (List<TaggedWord> tags : allTags) result.add(extract(rawText, tags, removeSubset, filterTypes));
    return result;
  }

  private class ResourceAndIob {
    Resource resource;
    List<Resource> ambiguities;
    boolean start;

    public ResourceAndIob(List<Resource> rankedResources, boolean start) {
      this.resource = rankedResources.get(0);
      this.ambiguities = new ArrayList<>();
      for (int i = 1; i < rankedResources.size(); i++)
        ambiguities.add(rankedResources.get(i));
      this.start = start;
    }
  }

  private class ResourceAndRank implements Comparable<ResourceAndRank> {
    Resource resource;
    float rate = 0f;

    public ResourceAndRank(Resource resource, float rate) {
      this.resource = resource;
      this.rate = rate;
    }

    @Override
    @SuppressWarnings("NotNull")
    public int compareTo(ResourceAndRank rank) {
      // Sort descending
      return Float.compare(rank.rate, rate);
    }
  }

  private List<Resource> rank(String context, List<Resource> resources) {
    final List<ResourceAndRank> ranked = new ArrayList<>();
    // Resources with specific classes are more important than things.
    for (Resource r : resources) {
      ResourceAndRank rr = new ResourceAndRank(r,
          (r.getClassTree() == null || r.getClassTree().size() <= 1) ? 0.0f : 0.5f);
      for (String variantLabel : r.getVariantLabel())
        // Check if it has dis-ambiguity.
        if (variantLabel.contains("(")) {
          rr.rate = rr.rate - 1;
          break;
        }
      ranked.add(rr);
    }
    Collections.sort(ranked);
    if (ranked.isEmpty() || ranked.get(0).rate < 0) return new ArrayList<>();
    return ranked.stream().map(it -> it.resource).collect(Collectors.toList());
  }

  private List<ResolvedEntityToken> extract(String context, List<TaggedWord> taggedWords,
                                            boolean removeSubset, FilterType... filterTypes) {
    final List<MatchedResource> clientResult = client.extract(taggedWords, removeSubset, filterTypes);
    final List<ResourceAndIob> alignedResources = new ArrayList<>();
    for (int i = 0; i < taggedWords.size(); i++) alignedResources.add(null);
    for (MatchedResource matchedResource : clientResult) {
      //We have supposed we haven't overlapped entities. if any, we remove second entity.
      boolean overlapped = false;
      for (int i = matchedResource.getStart(); i <= matchedResource.getEnd(); i++) {
        if (alignedResources.get(i) != null) overlapped = true;
        if (overlapped) continue;
        final List<Resource> all = new ArrayList<>();
        if (matchedResource.getResource() != null) all.add(matchedResource.getResource());
        if (matchedResource.getAmbiguities() != null)
          all.addAll(matchedResource.getAmbiguities());
        final List<Resource> rankedResources = rank(context, all);
        if (rankedResources.isEmpty()) continue;
        alignedResources.set(i, new ResourceAndIob(rankedResources, i == matchedResource.getStart()));
      }
    }

    final List<ResolvedEntityToken> result = new ArrayList<>();
    for (int i = 0; i < taggedWords.size(); i++) {
      final ResolvedEntityToken token = new ResolvedEntityToken();
      token.setWord(taggedWords.get(i).word());
      token.setPos(taggedWords.get(i).tag());
      final ResourceAndIob aligned = alignedResources.get(i);
      if (aligned == null) token.setIobType(IobType.Outside);
      else {
        token.setIobType(aligned.start ? IobType.Beginning : IobType.Inside);
        token.setResource(convert(aligned.resource));
        for (Resource ambiguities : aligned.ambiguities) token.getAmbiguities().add(convert(ambiguities));
      }
      result.add(token);
    }
    return result;
  }

  public void resolveByName(List<List<ResolvedEntityToken>> sentences) {
    final Map<String, ResolvedEntityTokenResource> cache = new HashMap<>();
    for (List<ResolvedEntityToken> sentence : sentences)
      for (ResolvedEntityToken token : sentence) {
        final String pos = token.getPos();
        if (cache.containsKey(token.getWord())) {
          token.getAmbiguities().add(0, token.getResource());
          token.setResource(cache.get(token.getWord()));
        }
        if (pos.equals("N") || pos.equals("Ne") && token.getResource() != null &&
            token.getResource().getClasses() != null &&
            !token.getResource().getClasses().isEmpty())
          cache.put(token.getWord(), token.getResource());
      }
  }

  final String prefix = "http://fkg.iust.ac.ir/ontology/";

  private boolean matchClass(ResolvedEntityToken token, String ontologyClass) {
    return token.getResource() != null && token.getResource().getClasses() != null
        && token.getResource().getClasses().contains(prefix + ontologyClass);
  }

  private void addToQueue(List<ResolvedEntityTokenResource> queue, ResolvedEntityTokenResource item) {
    if (queue.contains(item)) queue.remove(item);
    queue.add(item);
  }

  private void setResources(ResolvedEntityToken token, List<ResolvedEntityTokenResource> resources) {
    if (resources.size() == 0) return;
    token.setResource(resources.get(resources.size() - 1));
    for (int i = 0; i < resources.size() - 1; i++) token.getAmbiguities().add(resources.get(i));
  }

  public void resolvePronouns(List<List<ResolvedEntityToken>> sentences) {
    final List<ResolvedEntityTokenResource> allPersons = new ArrayList<>();
    final List<ResolvedEntityTokenResource> allPlaces = new ArrayList<>();
    final List<ResolvedEntityTokenResource> allOrganizations = new ArrayList<>();
    for (List<ResolvedEntityToken> sentence : sentences)
      for (ResolvedEntityToken token : sentence) {
        final String word = token.getWord();
        if (matchClass(token, "Person")) addToQueue(allPersons, token.getResource());
        if (matchClass(token, "Place") || matchClass(token, "Organisation"))
          addToQueue(allPlaces, token.getResource());
        if (matchClass(token, "Organisation")) addToQueue(allOrganizations, token.getResource());
        if (word.equals("او") || word.equals("وی")) setResources(token, allPersons);
        if (word.equals("اینجا") || word.equals("آنجا")) setResources(token, allPlaces);
        if (word.equals("آن‌ها") || word.equals("آنها")) setResources(token, allOrganizations);
      }
  }

  private ResolvedEntityTokenResource convert(Resource resource) {
    final ResolvedEntityTokenResource converted = new ResolvedEntityTokenResource();
    converted.setIri(resource.getIri());
    converted.setMainClass(resource.getInstanceOf());
    converted.setClasses(resource.getClassTree());
    converted.setResource(resource.getType() == null || resource.getType() == ResourceType.Resource);
    return converted;
  }
}
