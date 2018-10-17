package ir.ac.iust.dml.kg.raw.ner;

/**
 * Created by Majid on 31/05/2016.
 */
public class NerTaggedWord {

  private String word;
  private String posTag;
  private String nerTag;

  public NerTaggedWord() {
  }

  public NerTaggedWord(String word, String posTag, String nerTag) {
    this.word = word;
    this.posTag = posTag;
    this.nerTag = nerTag;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getPosTag() {
    return posTag;
  }

  public void setPosTag(String posTag) {
    this.posTag = posTag;
  }

  public String getNerTag() {
    return nerTag;
  }

  public void setNerTag(String nerTag) {
    this.nerTag = nerTag;
  }
}
