/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public class CorefChain {
  private ReferenceEntity referenceEntity;
  private List<Mention> mentions;

  public ReferenceEntity getReferenceEntity() {
    return referenceEntity;
  }

  public void setReferenceEntity(ReferenceEntity referenceEntity) {
    this.referenceEntity = referenceEntity;
  }

  public List<Mention> getMentions() {
    return mentions;
  }

  public void setMentions(List<Mention> mentions) {
    this.mentions = mentions;
  }

  public String toString() {
    String strReference="";
    String strMentions="";
    for(CoreLabel coreLabel:this.referenceEntity.getEntityTokens()) {
      strReference+=coreLabel.word()+" ";
    }

    for(Mention mention:this.getMentions()) {
      strMentions+=mention.getMentionCoreLabel().word()+",";
    }
    return  strMentions.substring(0,strMentions.length()-1) +" -> "+strReference;

  }
}
