/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreLabel;

public class Mention {
  private CoreLabel mentionCoreLabel;
  private String type;
  private int Number;
  private boolean isMofrad;
  private String posTag;
  private int index;

  public int getNumber() {
    return Number;
  }

  public void setNumber(int number) {
    Number = number;
  }

  public CoreLabel getMentionCoreLabel() {
    return mentionCoreLabel;
  }

  public void setMentionCoreLabel(CoreLabel mentionCoreLabel) {
    this.mentionCoreLabel = mentionCoreLabel;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isMofrad() {
    return isMofrad;
  }

  public void setMofrad(boolean isMofrad) {
    this.isMofrad = isMofrad;
  }

  public String getPosTag() {
    return posTag;
  }

  public void setPosTag(String posTag) {
    this.posTag = posTag;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
