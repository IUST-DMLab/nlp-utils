/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

public class DependencyInformation {
  private int position;
  private String lemma;
  private String features;
  private int head;
  private String relation;
  private String cPOS;

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getLemma() {
    return lemma;
  }

  public void setLemma(String lemma) {
    this.lemma = lemma;
  }

  public String getFeatures() {
    return features;
  }

  public void setFeatures(String features) {
    this.features = features;
  }

  public int getHead() {
    return head;
  }

  public void setHead(int head) {
    this.head = head;
  }

  public String getRelation() {
    return relation;
  }

  public void setRelation(String relation) {
    this.relation = relation;
  }

  public String getcPOS() {
    return cPOS;
  }

  public void setcPOS(String cPOS) {
    this.cPOS = cPOS;
  }
}
