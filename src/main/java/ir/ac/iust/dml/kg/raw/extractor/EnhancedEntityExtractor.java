package ir.ac.iust.dml.kg.raw.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.PathWalker;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;
import ir.ac.iust.nlp.jhazm.Stemmer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

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

  public List<List<ResolvedEntityToken>> extract(String rawText,
                                                 boolean removeSubset,
                                                 FilterType... filterTypes) {
    final List<List<TaggedWord>> allTags = POSTagger.tagRaw(rawText);
    final List<List<ResolvedEntityToken>> result = new ArrayList<>();
    for (List<TaggedWord> tags : allTags)
      result.add(extract(allTags, tags, removeSubset, filterTypes));
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

  private List<ResolvedEntityToken> extract(List<List<TaggedWord>> context,
                                            List<TaggedWord> taggedWords, boolean removeSubset,
                                            FilterType... filterTypes) {
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
        if (all.isEmpty()) continue;
        alignedResources.set(i, new ResourceAndIob(all, i == matchedResource.getStart()));
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
        for (Resource ambiguity : aligned.ambiguities) token.getAmbiguities().add(convert(ambiguity));
      }
      result.add(token);
    }
    return result;
  }

  private Map<String, String> textsOfAllArticles = null;

  private void loadTextOfAllArticles() {
    try {
      final Path path = ConfigReader.INSTANCE.getPath("wiki.folder.texts", "~/.pkg/data/texts");
      final List<Path> files = PathWalker.INSTANCE.getPath(path, null);

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      Type type = new TypeToken<Map<String, String>>() {
      }.getType();

      textsOfAllArticles = new HashMap<>();
      for (Path f : files) {
        final Map<String, String> map = gson.fromJson(new BufferedReader(
            new InputStreamReader(new FileInputStream(f.toFile()), "UTF-8")), type);
        textsOfAllArticles.putAll(map);
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      textsOfAllArticles = new HashMap<>();
    }
  }

  class WordCount {
    private int count;

    public WordCount(int count) {
      this.count = count;
    }
  }

  /**
   * check whether a tag is good for similarity calculation or not. in this way we don't count
   * stop words.
   *
   * @param tag POS tag of word
   * @return true if it is a bad tag
   */
  private boolean isBadTag(String tag) {
    return tag.equals("P") || tag.equals("CONJ") || tag.equals("PRO") || tag.equals("ADV");
  }

  private HashMap<String, HashMap<String, WordCount>> articleCache = new HashMap<>();

  /**
   * get words from article body. it has a cache to avoid calculation of frequently used articles.
   *
   * @param resource ambiguated resource
   * @return words and its counts
   */
  private HashMap<String, WordCount> getArticleWords(ResolvedEntityTokenResource resource) {
    final String url = resource.getIri();
    if (!url.startsWith("http://fkg.iust.ac.ir/resource/")) return null;
    String title = url.substring(31).replace("_", " ");
    if (articleCache.containsKey(title)) return articleCache.get(title);
    if (!textsOfAllArticles.containsKey(title)) return null;
    final String body = textsOfAllArticles.get(title);
    final HashMap<String, WordCount> articleWords = new HashMap<>();
    final List<List<TaggedWord>> sentences = POSTagger.tagRaw(body);
    for (List<TaggedWord> sentence : sentences)
      for (TaggedWord token : sentence) {
        if (isBadTag(token.tag())) continue;
        final String word = Stemmer.i().stem(token.word());
        WordCount wc = articleWords.get(word);
        if (wc == null) articleWords.put(word, new WordCount(1));
        else wc.count = wc.count + 1;
      }
    articleCache.put(title, articleWords);
    return articleWords;
  }

  /**
   * calculates similarity between words of two texts
   * @param text1 first text
   * @param text2 second text
   * @param ignoreCount ignores count of word in each texts and assume 1 instead count
   * @param word ignores word in product calculation
   * @return similarity of two texts
   */
  private float calculateSimilarity(HashMap<String, WordCount> text1, Map<String, WordCount> text2,
                                    boolean ignoreCount, String word) {
    double text1SquareNorm = 0f, text2SquareNorm = 0f, product = 0.f;
    if (ignoreCount) text1SquareNorm = text2.size();
    else for (WordCount count : text2.values()) text2SquareNorm += count.count * count.count;
    for (String word1 : text1.keySet()) {
      if (ignoreCount) {
        text1SquareNorm += 1;
        if (!word1.equals(word) && text2.containsKey(word1)) product += 1;
      } else {
        final int count1 = text1.get(word1).count;
        text1SquareNorm += count1 * count1;
        if (!word1.equals(word) && text2.containsKey(word1)) product += count1 * text2.get(word1).count;
      }
    }
    return (float) (product / (Math.sqrt(text1SquareNorm) * Math.sqrt(text2SquareNorm)));
  }

//  private List<Resource> rank(List<Resource> resources) {
//    final List<RankedObject<Resource>> ranked = new ArrayList<>();
//    // Resources with specific classes are more important than things.
//    for (Resource r : resources) {
//      RankedObject<Resource> rr = new RankedObject<>(r,
//          (r.getClassTree() == null || r.getClassTree().size() <= 1) ? 0.0f : 0.5f);
//      for (String variantLabel : r.getVariantLabel())
//        // Check if it has dis-ambiguity.
//        if (variantLabel.contains("(")) {
//          rr.rate = rr.rate - 1;
//          break;
//        }
//      ranked.add(rr);
//    }
//    Collections.sort(ranked);
//    if (ranked.isEmpty() || ranked.get(0).rate < 0) return new ArrayList<>();
//    return ranked.stream().map(it -> it.resource).collect(Collectors.toList());
//  }

  private void setDefaultRank(ResolvedEntityTokenResource resource) {
    if (resource == null) return;
    float rank = 0f;
    if (resource.getClasses().size() > 1) rank += 0.2;
//    if (resource.getIri().contains(")")) rank -= 0.3;
    if (resource.getIri().contains("ابهام")) rank -= 0.5;
    resource.setRank(rank);
  }

  /**
   * re-sort resources for each word with ambiguities based on its context
   *
   * @param sentences                      context of sentence.
   * @param contextDisambiguationThreshold a threshold to retain ambiguities or not.
   */
  public void disambiguateByContext(List<List<ResolvedEntityToken>> sentences,
                                    float contextDisambiguationThreshold) {
    if (textsOfAllArticles == null) loadTextOfAllArticles();
    if (textsOfAllArticles.isEmpty()) return;

    final Map<String, WordCount> contextWords = new HashMap<>();
    for (List<ResolvedEntityToken> sentence : sentences)
      for (ResolvedEntityToken token : sentence) {
        if (isBadTag(token.getPos())) continue;
        final String word = Stemmer.i().stem(token.getWord());
        WordCount wc = contextWords.get(word);
        if (wc == null) contextWords.put(word, new WordCount(1));
        else wc.count = wc.count + 1;
      }

    for (List<ResolvedEntityToken> sentence : sentences)
      for (ResolvedEntityToken token : sentence) {
        if (token.getAmbiguities().isEmpty()) continue;
        final List<ResolvedEntityTokenResource> allResources = new ArrayList<>();
        setDefaultRank(token.getResource());
        allResources.add(token.getResource());
        for (ResolvedEntityTokenResource a : token.getAmbiguities()) {
          setDefaultRank(a);
          allResources.add(a);
        }
        if (allResources.size() == 1) {
          token.getAmbiguities().clear();
          token.setResource(allResources.get(0));
          continue;
        }
        for (ResolvedEntityTokenResource rr : allResources) {
          if (rr == null) continue;
          final HashMap<String, WordCount> articleWords = getArticleWords(rr);
          if (articleWords != null)
            rr.setRank(rr.getRank() + calculateSimilarity(articleWords, contextWords,
                false, token.getWord()));
        }
        Collections.sort(allResources);

        if (allResources.size() > 0) {
          final ResolvedEntityTokenResource r = allResources.get(0);
          if (r.getRank() >= contextDisambiguationThreshold) token.setResource(r);
        }
        token.getAmbiguities().clear();
        for (int i = 1; i < allResources.size(); i++) {
          final ResolvedEntityTokenResource r = allResources.get(i);
          if (r.getRank() >= contextDisambiguationThreshold) token.getAmbiguities().add(r);
        }
      }
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
