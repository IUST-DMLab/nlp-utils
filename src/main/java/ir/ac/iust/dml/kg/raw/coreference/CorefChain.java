package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreLabel;

import java.lang.String;import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 1/29/17 8:53 PM
 */
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

    public String toString()
    {
        String strReference="";
        String strMentions="";
        for(CoreLabel coreLabel:this.referenceEntity.getEntityTokens())
        {
            strReference+=coreLabel.word()+" ";
        }

        for(Mention mention:this.getMentions())
        {
            strMentions+=mention.getMentionCoreLabel().word()+",";
        }
        return  strMentions.substring(0,strMentions.length()-1) +" -> "+strReference;

    }
}
