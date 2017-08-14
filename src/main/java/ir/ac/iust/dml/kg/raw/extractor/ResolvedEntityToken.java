package ir.ac.iust.dml.kg.raw.extractor;

import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ResolvedEntityToken {
  private TaggedWord word;
  private IobType iobType;
  private ResolvedEntityTokenResource resource;
  private List<ResolvedEntityTokenResource> ambiguities = new ArrayList<>();

  public TaggedWord getWord() {
    return word;
  }

  void setWord(TaggedWord word) {
    this.word = word;
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
}
