package ir.ac.iust.dml.kg.raw.extractor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ResolvedEntityToken {
  private String word;
  private String pos;
  private IobType iobType;
  private ResolvedEntityTokenResource resource;
  private List<ResolvedEntityTokenResource> ambiguities = new ArrayList<>();

  public String getWord() {
    return word;
  }

  void setWord(String word) {
    this.word = word;
  }

  public String getPos() {
    return pos;
  }

  void setPos(String pos) {
    this.pos = pos;
  }

  public IobType getIobType() {
    return iobType;
  }

  void setIobType(IobType iobType) {
    this.iobType = iobType;
  }

  public ResolvedEntityTokenResource getResource() {
    return resource;
  }

  void setResource(ResolvedEntityTokenResource resource) {
    this.resource = resource;
  }

  public List<ResolvedEntityTokenResource> getAmbiguities() {
    return ambiguities;
  }

  void setAmbiguities(List<ResolvedEntityTokenResource> ambiguities) {
    this.ambiguities = ambiguities;
  }
}
