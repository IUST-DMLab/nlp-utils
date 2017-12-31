/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.DependencyParser;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.PathWalker;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;
import kotlin.text.Regex;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class EnhancedEntityExtractor {

  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final static Type EXPORT_GSON_TYPE = new TypeToken<List<List<ResolvedEntityToken>>>() {
  }.getType();

  private final ResourceExtractionWrapper client;
  private static final Logger logger = LoggerFactory.getLogger(EnhancedEntityExtractor.class);

  public EnhancedEntityExtractor(String serviceAddress) {
    this.client = new ResourceExtractionWrapper(serviceAddress);
  }

  public EnhancedEntityExtractor() {
    this.client = new ResourceExtractionWrapper(ConfigReader.INSTANCE.getString("resource.extractor.url",
        "http://localhost:8094"));
  }

  public List<List<ResolvedEntityToken>> extract(String rawText) {
    return extract(rawText, true, FilterType.FilteredWords, FilterType.CommonPosTags, FilterType.Properties);
  }

  public List<List<ResolvedEntityToken>> extract(String rawText,
                                                 boolean removeSubset,
                                                 FilterType... filterTypes) {
    final List<List<TaggedWord>> allTags = POSTagger.tagRaw(rawText);
    final List<List<ResolvedEntityToken>> result = new ArrayList<>();
    for (List<TaggedWord> tags : allTags)
      result.add(extract(tags, removeSubset, filterTypes));
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

  private List<ResolvedEntityToken> extract(List<TaggedWord> taggedWords, boolean removeSubset,
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
      final Path path = ConfigReader.INSTANCE.getPath("wiki.folder.texts",
          "~/.pkg/data/texts");
      final List<Path> files = PathWalker.INSTANCE.getPath(path);

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
    return tag.equals("P") || tag.equals("Pe") || tag.equals("POSTP") ||
        tag.equals("DET") || tag.equals("NUM") || tag.equals("PUNC") ||
        tag.equals("CONJ") || tag.equals("PRO") || tag.equals("ADV") || tag.equals("V");
  }

  private HashMap<String, HashMap<String, WordCount>> articleCache = new HashMap<>();

  /**
   * get words from article body. it has a cache to avoid calculation of frequently used articles.
   *
   * @param title resource title
   * @return words and its counts
   */
  private HashMap<String, WordCount> getArticleWords(String title) {
    if (articleCache.containsKey(title)) return articleCache.get(title);
    if (!textsOfAllArticles.containsKey(title)) return null;
    final String body = textsOfAllArticles.get(title);
    final HashMap<String, WordCount> articleWords = new HashMap<>();
    final List<List<TaggedWord>> sentences = POSTagger.tagRaw(body);
    int sentenceNumber = 0;
    for (List<TaggedWord> sentence : sentences) {
      if (sentenceNumber > 40) break;
      sentenceNumber++;
      for (TaggedWord token : sentence) {
        if (isBadTag(token.tag())) continue;
        final String word = token.word();
        WordCount wc = articleWords.get(word);
        if (wc == null) articleWords.put(word, new WordCount(1));
        else wc.count = wc.count + 1;
      }
    }
    articleCache.put(title, articleWords);
    return articleWords;
  }

  /**
   * calculates similarity between words of two texts
   *
   * @param text1       first text
   * @param text2       second text
   * @param ignoreCount ignores count of word in each texts and assume 1 instead count
   * @param word        ignores word in product calculation
   * @return similarity of two texts
   */
  private float calculateSimilarity(Map<String, WordCount> text1, Map<String, WordCount> text2,
                                    boolean ignoreCount, String word) {
    double text1SquareNorm = 0f, text2SquareNorm = 0f, product = 0.f;
    if (ignoreCount) text2SquareNorm = text2.size();
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

  private void setDefaultRankMultiplier(ResolvedEntityTokenResource resource) {
    if (resource == null) return;
    float rank = 1f;
//    if (resource.getClasses().size() > 1) rank *= 1.1;
//    if (resource.getClasses().contains(prefix + "Thing")) rank *= 1.1;
    if (resource.getClasses().size() == 1) rank *= 1.1;
    if (resource.getClasses().contains(prefix + "Village")) rank *= 0.01;
    if (resource.getClasses().contains(prefix + "Work")) rank *= 0.01;
    if (resource.getClasses().contains(prefix + "Film")
        || resource.getIri().contains("(فیلم)")) rank *= 0.01;
    if (resource.getIri().contains("ابهام")) rank *= 0.01;
    if (resource.getIri().contains("ابهام")) rank *= 0.01;
//    if (resource.getIri().contains(")")) rank -= 0.3;
    if (resource.getIri().contains("(")) rank *= 0.07;
    resource.setRank(rank);
  }

  private List<ResolvedEntityToken> getContext(List<List<ResolvedEntityToken>> sentences,
                                               int sentenceIndex, int contextLength) {
    final List<ResolvedEntityToken> context = new ArrayList<>();
    for (int i = sentenceIndex - 1; i > sentenceIndex - contextLength && i >= 0; i--)
      context.addAll(sentences.get(i));
    context.addAll(sentences.get(sentenceIndex));
    for (int i = sentenceIndex + 1; i < sentenceIndex + contextLength && i <= sentences.size() - 1; i++)
      context.addAll(sentences.get(i));
    return context;
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

    for (int sentenceIndex = 0; sentenceIndex < sentences.size(); sentenceIndex++) {
      final List<ResolvedEntityToken> sentence = sentences.get(sentenceIndex);

      final List<ResolvedEntityToken> context = getContext(sentences, sentenceIndex, 1);
      final Map<String, WordCount> contextWords = new HashMap<>();
      for (ResolvedEntityToken token : context) {
        if (isBadTag(token.getPos())) continue;
        final String word = token.getWord();
        WordCount wc = contextWords.get(word);
        if (wc == null) contextWords.put(word, new WordCount(1));
        else wc.count = wc.count + 1;
      }

      for (ResolvedEntityToken token : sentence) {
        if (token.getResource() == null) continue;
        final List<ResolvedEntityTokenResource> allResources = new ArrayList<>();
        setDefaultRankMultiplier(token.getResource());
        allResources.add(token.getResource());
        for (ResolvedEntityTokenResource a : token.getAmbiguities()) {
          setDefaultRankMultiplier(a);
          allResources.add(a);
        }
        for (ResolvedEntityTokenResource rr : allResources) {
          if (rr == null) continue;
          final String url = rr.getIri();
          if (!url.startsWith("http://fkg.iust.ac.ir/resource/")) continue;
          String title = url.substring(31).replace("_", " ");
          final HashMap<String, WordCount> articleWords = getArticleWords(title);
          float similarity;
          if (articleWords != null) {
            similarity = calculateSimilarity(contextWords, articleWords,
                true, token.getWord());
            logger.trace(String.format("similarity between %s and %s is %f.", token.getWord(), title, similarity));
            if (similarity == 0) similarity = 0.001f;
          } else similarity = 0f;
          if (token.getWord().equals(title)) similarity *= 3;
          rr.setRank(rr.getRank() * similarity);
        }
        Collections.sort(allResources);

        if (allResources.size() > 0) {
          final ResolvedEntityTokenResource r = allResources.get(0);
          if (r.getRank() >= contextDisambiguationThreshold) token.setResource(r);
          else {
            token.setIobType(IobType.Outside);
            token.setResource(null);
          }
        }
        token.getAmbiguities().clear();
        for (int i = 1; i < allResources.size(); i++) {
          final ResolvedEntityTokenResource r = allResources.get(i);
          if (r.getRank() >= contextDisambiguationThreshold) token.getAmbiguities().add(r);
        }
      }
    }
  }

  public void resolveByName(List<List<ResolvedEntityToken>> sentences) {
    final Map<String, ResolvedEntityTokenResource> cache = new HashMap<>();
    for (List<ResolvedEntityToken> sentence : sentences)
      for (ResolvedEntityToken token : sentence) {
        final String pos = token.getPos();
        if (token.getResource() == null && pos.equals("N") || pos.equals("Ne") && cache.containsKey(token.getWord())) {
          token.getAmbiguities().add(0, token.getResource());
          token.setResource(cache.get(token.getWord()));
        }
        if (pos.equals("N") || pos.equals("Ne") && token.getResource() != null &&
            token.getResource().getClasses() != null &&
            !token.getResource().getClasses().isEmpty())
          cache.put(token.getWord(), token.getResource());
      }
  }

  //  final String prefix = URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgOntologyPrefix() + ":");
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

  public void dependencyParse(List<List<ResolvedEntityToken>> sentences) {
    for (List<ResolvedEntityToken> sentence : sentences) {
      List<TaggedWord> taggedWords = new ArrayList<>();
      for (ResolvedEntityToken token : sentence) taggedWords.add(new TaggedWord(token.getWord(), token.getPos()));
      final ConcurrentDependencyGraph parseTree = DependencyParser.parse(taggedWords);
      if (parseTree != null && parseTree.nTokenNodes() == taggedWords.size()) {
        for (int i = 1; i <= parseTree.nTokenNodes(); i++) {
          final ConcurrentDependencyNode node = parseTree.getDependencyNode(i);
          DependencyInformation info = new DependencyInformation();
          info.setPosition(Integer.parseInt(node.getLabel("ID")));
          info.setLemma(node.getLabel("LEMMA"));
          info.setcPOS(node.getLabel("CPOSTAG"));
          info.setFeatures(node.getLabel("FEATS"));
          String headIdLabel = node.getHead().getLabel("ID");
          info.setHead(headIdLabel.isEmpty() ? 0 : Integer.parseInt(headIdLabel));
          info.setRelation(node.getLabel("DEPREL"));
          sentence.get(i - 1).setDep(info);
        }
      }
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

  private List<List<ResolvedEntityToken>> extract(Integer maxAmbiguities, boolean disambiguateByContext,
                                                  Float contextDisambiguationThreshold, boolean resolveByName,
                                                  boolean resolvePronouns, boolean buildDependencies, String value) {
    final List<List<ResolvedEntityToken>> extracted = extract(value);
    if (disambiguateByContext) disambiguateByContext(extracted, contextDisambiguationThreshold);
    if (resolveByName) resolveByName(extracted);
    if (resolvePronouns) resolvePronouns(extracted);
    if (buildDependencies) dependencyParse(extracted);
    if (maxAmbiguities != null) {
      for (List<ResolvedEntityToken> sentence : extracted)
        for (ResolvedEntityToken token : sentence)
          if (token.getAmbiguities().size() > maxAmbiguities) {
            for (int i = token.getAmbiguities().size() - 1; i > 1; i--)
              token.getAmbiguities().remove(i);
          }
    }
    return extracted;
  }

  public void exportWiki(Path path, Integer maxAmbiguities,
                         boolean disambiguateByContext, Float contextDisambiguationThreshold,
                         boolean resolveByName, boolean resolvePronouns, boolean buildDependencies) throws IOException {
    final List<Path> files = PathWalker.INSTANCE.getPath(path, new Regex("\\d+\\.json"));
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    for (Path f : files) {
      final Path outputFolder = f.getParent().resolve(f.getFileName() + "_output");
      if (Files.notExists(outputFolder)) Files.createDirectories(outputFolder);
      final Map<String, String> map = gson.fromJson(new BufferedReader(
          new InputStreamReader(new FileInputStream(f.toFile()), "UTF-8")), type);
      for (final String key : map.keySet()) {
        final String value = map.get(key);
        try {
          final List<List<ResolvedEntityToken>> result = extract(maxAmbiguities, disambiguateByContext,
              contextDisambiguationThreshold, resolveByName, resolvePronouns, buildDependencies, value);
          exportToFile(outputFolder.resolve(key + ".json"), result);
        } catch (Throwable th) {
          th.printStackTrace();
        }
      }
    }
  }

  public void exportFolder(Path path, String pattern, Integer maxAmbiguities,
                           boolean disambiguateByContext, Float contextDisambiguationThreshold,
                           boolean resolveByName, boolean resolvePronouns, boolean buildDependencies) throws IOException {
    if (pattern == null) pattern = ".*\\.txt";
    final List<Path> files = PathWalker.INSTANCE.getPath(path, new Regex(pattern));
    for (int fileIndex = 0; fileIndex < files.size(); fileIndex++) {
      final Path file = files.get(fileIndex);
      final Path outputFolder = file.getParent().resolve("output");
      if (Files.notExists(outputFolder)) Files.createDirectories(outputFolder);
      final Path outputPath = outputFolder.resolve(file.getFileName() + ".json");
      logger.warn(String.format("writing file %s (file %d of %d) ...", file.toAbsolutePath(), fileIndex, files.size()));
      if (Files.exists(outputPath)) {
        logger.warn(String.format("file %s is existed. Skipping %s ...",
            outputPath.toAbsolutePath(), file.toAbsolutePath()));
        continue;
      }
      try (BufferedReader in =
               new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()),
                   "UTF8"))) {
        final List<List<ResolvedEntityToken>> list = new ArrayList<>();
        while (true) {
          final String line = in.readLine();
          logger.info(line);
          if (line == null) break;
          try {
            final List<List<ResolvedEntityToken>> extracted = extract(maxAmbiguities, disambiguateByContext,
                contextDisambiguationThreshold, resolveByName, resolvePronouns, buildDependencies, line);
            list.addAll(extracted);
          } catch (Throwable th) {
            th.printStackTrace();
          }
        }
        exportToFile(outputPath, list);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean exportToFile(Path path, List<List<ResolvedEntityToken>> sentences) {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), "UTF-8")) {
      gson.toJson(sentences, writer);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public static List<List<ResolvedEntityToken>> importFromFile(Path path) {
    try (Reader reader = new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8")) {
      return gson.fromJson(reader, EXPORT_GSON_TYPE);
    } catch (IOException e) {
      return null;
    }
  }

}
