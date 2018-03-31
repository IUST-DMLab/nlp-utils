/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.resource.extractor.client.ExtractorClient;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ResourceExtractionWrapper {

  private static ResourceExtractionWrapper instance = null;
  public static ResourceExtractionWrapper i() {
    if(instance == null)
      instance = new ResourceExtractionWrapper(ConfigReader.INSTANCE.getString("resource.extractor.url",
              "http://localhost:8094"));
    return instance;
  }

  private final ExtractorClient client;
  private static final Set<String> ignoredWords = new HashSet<>();

  private boolean isInIgnoredWords(String word) {
    if (ignoredWords.isEmpty()) loadIgnoredWords();
    return ignoredWords.contains(word);
  }

  private void loadIgnoredWords() {
    final Path path = ConfigReader.INSTANCE.getPath("raw.enhanced.extractor.ignored.words",
        "~/.pkg/ee_ignored.txt").toAbsolutePath();
    try {
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("ee_ignore.txt")) {
          Files.copy(in, path);
        }
      }
      final List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
      ignoredWords.addAll(lines);
    } catch (Throwable th) {
      th.printStackTrace();
    }
  }

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
    for (TaggedWord taggedWord : taggedWords)
      if (taggedWord.word().contains(" ")) taggedWord.setWord(taggedWord.word().replace(' ', '\u200C'));
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
      if (filterTypeSet.contains(FilterType.FilteredWords)) {
        if (resource.getStart() == resource.getEnd() && isInIgnoredWords(taggedWords.get(resource.getStart()).word()))
          continue;
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

      if (mustFilter(taggedWords, resource, resource.getResource(), filterTypes))
        resource.setResource(null);
      final List<Resource> filteredAmbiguities = new ArrayList<>();
      for (Resource ambiguity : resource.getAmbiguities())
        if (mustFilter(taggedWords, resource, ambiguity, filterTypes) || filterTypeSet.contains(FilterType.Ambiguities))
          filteredAmbiguities.add(ambiguity);
      resource.getAmbiguities().removeAll(filteredAmbiguities);

      if (resource.getResource() == null && resource.getAmbiguities().size() == 1) {
        resource.setResource(resource.getAmbiguities().get(0));
        resource.getAmbiguities().clear();
      }
      if (resource.getResource() != null || !resource.getAmbiguities().isEmpty())
        filteredResult.add(resource);
    }
    return filteredResult;
  }

  private boolean mustFilter(List<TaggedWord> taggedWords, MatchedResource matchedResource,
                             Resource resource, FilterType... filterTypes) {
    for (FilterType filterType : filterTypes) {
      if (mustFilter(taggedWords, matchedResource, resource, filterType)) return true;
    }
    return false;
  }

  private boolean mustFilter(List<TaggedWord> taggedWords, MatchedResource matchedResource,
                             Resource resource, FilterType filterType) {
    if (resource == null) return true;
    switch (filterType) {
      case AnyResources:
        if (resource.getType() == null || resource.getType() == ResourceType.Resource) return true;
        break;
      case Villages:
        if (resource.getInstanceOf() != null && resource.getInstanceOf().contains("Village")) return true;
        break;
      case Properties:
        if ((resource.getType() != null && resource.getType() == ResourceType.Property)
            || (resource.getType() == null && resource.getIri() != null && resource.getIri().contains("ontology")))
          return true;
        break;
      case Things:
        if (resource.getInstanceOf() == null || resource.getInstanceOf().contains("Thing")) return true;
        break;
      case EmptyClassTree:
        if (resource.getClassTree() == null || resource.getClassTree().isEmpty()) return true;
        break;
      case NotNullDisambiguatedFrom:
        if (resource.getDisambiguatedFrom() != null && !resource.getDisambiguatedFrom().isEmpty()) return true;
        for (String variantLabel : resource.getVariantLabel()) {
          if (variantLabel.contains(")")) return true;
        }
        break;
      case NotMatchedLabels:
        final String label = resource.getLabel() != null ? resource.getLabel() :
            (resource.getIri() == null ? "" : resource.getIri().substring(resource.getIri().lastIndexOf('/')));
        StringBuilder matchedLabel = new StringBuilder();
        for (int i = matchedResource.getStart(); i <= matchedResource.getEnd(); i++)
          matchedLabel.append(taggedWords.get(i).word()).append(' ');
        matchedLabel.setLength(matchedLabel.length() - 1);
        if (!label.equals(matchedLabel.toString())) return true;
    }
    return false;
  }

  private boolean isBadTagForMatchedResource(TaggedWord taggedWord) {
    final String tag = taggedWord.tag();
    return tag.equals("P") || tag.equals("Pe") || tag.equals("CONJ") || tag.equals("POSTP") || tag.equals("PUNC") ||
        tag.equals("DET") || tag.equals("NUM") || tag.equals("V") || tag.equals("PRO") || tag.equals("ADV");
  }
}
