package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 1/29/17 8:54 PM
 */
public class ReferenceEntity {
    List<CoreLabel> entityTokens;
    String type;
    int Number;
    boolean isMofrad;
    int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public boolean isMofrad() {
        return isMofrad;
    }

    public void setMofrad(boolean isMofrad) {
        this.isMofrad = isMofrad;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public List<CoreLabel> getEntityTokens() {
        return entityTokens;
    }

    public void setEntityTokens(List<CoreLabel> entityTokens) {
        this.entityTokens = entityTokens;
    }



}
