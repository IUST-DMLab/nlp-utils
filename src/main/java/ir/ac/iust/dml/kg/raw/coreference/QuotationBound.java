/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public class QuotationBound {
  private List<CoreLabel> tellerBoundCoreLabels;
  private List<CoreLabel> quotationBoundCoreLabels;
  private String quotationString;
  private String tellerString;


  public String getQuotationString() {
    return quotationString;
  }

  public void setQuotationString(String quotationString) {
    this.quotationString = quotationString;
  }

  public String getTellerString() {
    return tellerString;
  }

  public void setTellerString(String tellerString) {
    this.tellerString = tellerString;
  }


  public List<CoreLabel> getTellerBoundCoreLabels() {
    return tellerBoundCoreLabels;
  }

  public void setTellerBoundCoreLabels(List<CoreLabel> tellerBoundCoreLabels) {
    this.tellerBoundCoreLabels = tellerBoundCoreLabels;
  }

  public List<CoreLabel> getQuotationBoundCoreLabels() {
    return quotationBoundCoreLabels;
  }

  public void setQuotationBoundCoreLabels(List<CoreLabel> quotationBoundCoreLabels) {
    this.quotationBoundCoreLabels = quotationBoundCoreLabels;
  }

}
