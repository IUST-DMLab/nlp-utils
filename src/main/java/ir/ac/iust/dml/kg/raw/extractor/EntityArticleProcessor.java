package ir.ac.iust.dml.kg.raw.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.PathWalker;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class EntityArticleProcessor {

  private static final Logger logger = LoggerFactory.getLogger(EntityArticleProcessor.class);
  private final ResourceExtractionWrapper client;
  private HashMap<String, HashMap<String, WordInfo>> articleWordCache = new HashMap<>();
  private HashMap<String, HashSet<String>> articleEntityCache = new HashMap<>();
  private HashMap<String, HashSet<String>> articleEntityVerbCache = new HashMap<>();
  private HashMap<String, HashMap<String, String>> articleEntityPosCache = new HashMap<>();
  private Map<String, String> textsOfAllArticles = null;
  private Map<String, List<List<TaggedWord>>> articlePosTags = new HashMap<>();

  EntityArticleProcessor() {
    this.client = ResourceExtractionWrapper.i();
  }
  EntityArticleProcessor(ResourceExtractionWrapper client) {
    this.client = client;
  }

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
        map.forEach((title, body) -> {
          body = body.replaceAll("\\s+\\(.*\\)\\s+", " ").replaceAll("(?U)\\[\\d+]", "");
          textsOfAllArticles.put(title, body);
          textsOfAllArticles.put(URIs.INSTANCE.getFkgResourceUri(title), body);
        });
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      textsOfAllArticles = new HashMap<>();
    }
  }

  String getArticleBody(String title) {
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    if (!textsOfAllArticles.containsKey(title)) return null;
    return textsOfAllArticles.get(title);
  }

  /**
   * get words from article body. it has a cache to avoid calculation of frequently used articles.
   *
   * @param titleOrIri resource title
   * @return words and its counts
   */
  HashMap<String, WordInfo> getArticleWords(String titleOrIri) {
    if (articleWordCache.containsKey(titleOrIri)) return articleWordCache.get(titleOrIri);
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    if (!textsOfAllArticles.containsKey(titleOrIri)) return null;
    final HashMap<String, WordInfo> articleWords = new HashMap<>();
    final List<List<TaggedWord>> sentences = getArticlePosTags(titleOrIri);
    int sentenceNumber = 0;
    for (List<TaggedWord> sentence : sentences) {
      if (sentenceNumber > 40) break;
      sentenceNumber++;
      for (TaggedWord token : sentence) {
        if (Utils.isBadTag(token.tag())) continue;
        final String word = token.word();
        WordInfo wc = articleWords.get(word);
        if (wc == null) articleWords.put(word, new WordInfo(1));
        else wc.count = wc.count + 1;
      }
    }
    articleWordCache.put(titleOrIri, articleWords);
    return articleWords;
  }

  HashSet<String> getArticleResources(String titleOrIri) {
    if (articleEntityCache.containsKey(titleOrIri)) return articleEntityCache.get(titleOrIri);
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    final List<List<TaggedWord>> sentences = getArticlePosTags(titleOrIri);
    final HashSet<String> result = new HashSet<>();
    for (List<TaggedWord> sentence : sentences) {
      final List<MatchedResource> extracted = client.extract(sentence,
          false, FilterType.FilteredWords, FilterType.CommonPosTags, FilterType.Properties);
      for(MatchedResource mr: extracted) {
        if(mr.getResource() != null) result.add(mr.getResource().getIri());
        for(Resource ar : mr.getAmbiguities()) {
          result.add(ar.getIri());
        }
      }
    }
    articleEntityCache.put(titleOrIri, result);
    return result;
  }

  HashSet<String> getArticleVerbs(String titleOrIri) {
    if (articleEntityVerbCache.containsKey(titleOrIri)) return articleEntityVerbCache.get(titleOrIri);
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    final List<List<TaggedWord>> sentences = getArticlePosTags(titleOrIri);
    final HashSet<String> result = new HashSet<>();
    if (sentences == null) return result;
    final String uri = URIs.INSTANCE.getFkgResourceUri(titleOrIri);
    for (List<TaggedWord> sentence : sentences) {
      final List<MatchedResource> extracted = client.extract(sentence,
          false, FilterType.FilteredWords, FilterType.CommonPosTags, FilterType.Properties);
      for(MatchedResource mr: extracted) {
        boolean found = false;
        if(mr.getResource() != null && Objects.equals(mr.getResource().getIri(), uri)) found = true;
        if(!found)
          for(Resource a: mr.getAmbiguities())
            if(Objects.equals(a.getIri(), uri)) found = true;
        if(found)
          for(int i = mr.getEnd() + 1; i < sentence.size(); i++)
            if(sentence.get(i).tag().equals("V"))
              result.add(sentence.get(i).word());
      }
    }
    articleEntityVerbCache.put(titleOrIri, result);
    return result;
  }

  HashMap<String, String> getArticleEntityPos(String titleOrIri) {
    if (articleEntityPosCache.containsKey(titleOrIri)) return articleEntityPosCache.get(titleOrIri);
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    final List<List<TaggedWord>> sentences = getArticlePosTags(titleOrIri);
    final HashMap<String, String> result = new HashMap<>();
    final String uri = URIs.INSTANCE.getFkgResourceUri(titleOrIri);
    for (List<TaggedWord> sentence : sentences) {
      final List<MatchedResource> extracted = client.extract(sentence,
          false, FilterType.FilteredWords, FilterType.CommonPosTags, FilterType.Properties);
      for(MatchedResource mr: extracted) {
        boolean found = false;
        if(Objects.equals(mr.getResource().getIri(), uri)) found = true;
        if(!found)
          for(Resource a: mr.getAmbiguities())
            if(Objects.equals(a.getIri(), uri)) found = true;
        if(found)
          for(int i = mr.getStart(); i <= mr.getEnd(); i++)
            result.put(sentence.get(i).word(), sentence.get(i).tag());
      }
    }
    articleEntityPosCache.put(titleOrIri, result);
    return result;
  }

  List<List<TaggedWord>> getArticlePosTags(String titleOrIri) {
    if (articlePosTags.containsKey(titleOrIri)) return articlePosTags.get(titleOrIri);
    if(textsOfAllArticles == null) loadTextOfAllArticles();
    if (!textsOfAllArticles.containsKey(titleOrIri)) return null;
    final String body = getArticleBody(titleOrIri);
    final List<List<TaggedWord>> sentences = POSTagger.tagRaw(body);
    articlePosTags.put(titleOrIri, sentences);
    return sentences;
  }
}
