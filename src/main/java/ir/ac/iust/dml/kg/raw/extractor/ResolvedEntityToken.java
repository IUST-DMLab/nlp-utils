/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  public ResolvedEntityToken() {
  }

  public ResolvedEntityToken(ResolvedEntityToken copyFrom) {
    this.word = copyFrom.word;
    this.pos = copyFrom.pos;
    this.dep = copyFrom.dep;
    this.iobType = copyFrom.iobType;
    this.resource = copyFrom.resource;
    this.ambiguities = copyFrom.ambiguities;
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
