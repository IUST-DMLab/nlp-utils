/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class ResolvedEntityToken {
  private String word;
  private String pos;
  private DependencyInformation dep;
  private IobType iobType;
  private Set<Integer> phraseMates;
  private Map<Integer, DependencyInformation> dependencyMates;
  private ResolvedEntityTokenResource resource;
  private List<ResolvedEntityToken> shrunkWords;
  private List<ResolvedEntityTokenResource> ambiguities = new ArrayList<>();

  public static List<List<ResolvedEntityToken>> copySentences(List<List<ResolvedEntityToken>> sentences) {
    List<List<ResolvedEntityToken>> copy = new ArrayList<>();
    for (List<ResolvedEntityToken> s1 : sentences) {
      copy.add(copySentence(s1));
    }
    return copy;
  }

  public static List<ResolvedEntityToken> copySentence(List<ResolvedEntityToken> sentence) {
    List<ResolvedEntityToken> copy = new ArrayList<>();
    for (ResolvedEntityToken t : sentence) copy.add(t.copy());
    return copy;
  }

  public ResolvedEntityToken copy() {
    ResolvedEntityToken copy = new ResolvedEntityToken();
    copy.word = this.word;
    copy.pos = this.pos;
    if (dep != null) copy.dep = this.dep.copy();
    copy.iobType = this.iobType;
    if (this.phraseMates != null) {
      copy.phraseMates = new HashSet<>();
      copy.phraseMates.addAll(this.phraseMates);
    }
    if (this.dependencyMates != null) {
      copy.dependencyMates = new HashMap<>();
      for (Integer key : this.dependencyMates.keySet())
        copy.dependencyMates.put(key, this.dependencyMates.get(key).copy());
    }
    if (this.resource != null) copy.resource = this.resource.copy();
    if (this.shrunkWords != null) {
      copy.shrunkWords = new ArrayList<>();
      for (ResolvedEntityToken token : this.shrunkWords)
        copy.shrunkWords.add(token.copy());
    }
    for (ResolvedEntityTokenResource am : ambiguities)
      copy.ambiguities.add(am.copy());
    return copy;
  }

  public ResolvedEntityToken() {
  }

  public Set<Integer> getPhraseMates() {
    return phraseMates;
  }

  public void setPhraseMates(Set<Integer> phraseMates) {
    this.phraseMates = phraseMates;
  }

  public Map<Integer, DependencyInformation> getDependencyMates() {
    return dependencyMates;
  }

  public void setDependencyMates(Map<Integer, DependencyInformation> dependencyMates) {
    this.dependencyMates = dependencyMates;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getPos() {
    return pos;
  }

  public void setPos(String pos) {
    this.pos = pos;
  }

  public DependencyInformation getDep() {
    return dep;
  }

  public List<ResolvedEntityToken> getShrunkWords() {
    return shrunkWords;
  }

  public void setShrunkWords(List<ResolvedEntityToken> shrunkWords) {
    this.shrunkWords = shrunkWords;
  }

  public void setDep(DependencyInformation dep) {
    this.dep = dep;
  }

  public IobType getIobType() {
    return iobType;
  }

  public void setIobType(IobType iobType) {
    this.iobType = iobType;
  }

  public ResolvedEntityTokenResource getResource() {
    return resource;
  }

  public void setResource(ResolvedEntityTokenResource resource) {
    this.resource = resource;
  }

  public List<ResolvedEntityTokenResource> getAmbiguities() {
    return ambiguities;
  }

  public void setAmbiguities(List<ResolvedEntityTokenResource> ambiguities) {
    this.ambiguities = ambiguities;
  }
}
