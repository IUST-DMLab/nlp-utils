/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

public class DependencyInformation {
  private int position;
  private String lemma;
  private String features;
  private int head;
  private String relation;
  private String cPOS;

  public DependencyInformation() {
  }

  public DependencyInformation copy() {
    DependencyInformation copy = new DependencyInformation();
    copy.position = this.position;
    copy.lemma = this.lemma;
    copy.features = this.features;
    copy.head = this.head;
    copy.relation = this.relation;
    copy.cPOS = this.cPOS;
    return copy;
  }

  public DependencyInformation(ConcurrentDependencyNode node) {
    this.cPOS = node.getLabel("CPOSTAG");
    this.features = node.getLabel("FEATS");
    final String headIdLabel = node.getHead().getLabel("ID");
    this.head = headIdLabel.isEmpty() ? 0 : Integer.parseInt(headIdLabel);
    this.lemma = node.getLabel("LEMMA");
    this.position = Integer.parseInt(node.getLabel("ID"));
    this.relation = node.getLabel("DEPREL");
  }

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
