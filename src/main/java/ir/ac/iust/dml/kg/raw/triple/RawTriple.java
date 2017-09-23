/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings(value = {"unused", "WeakerAccess"})
public class RawTriple {
  private String module;
  private String subject;
  private String predicate;
  private boolean needsMapping;
  private String object;
  private String rawText;
  private String sourceUrl;
  private Long extractionTime;
  private String version;
  private Double accuracy;
  private Map<String, String> metadata = new HashMap<>();

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public RawTriple module(String module) {
    this.module = module;
    return this;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public RawTriple subject(String subject) {
    this.subject = subject;
    return this;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public RawTriple predicate(String predicate) {
    this.predicate = predicate;
    return this;
  }

  public boolean isNeedsMapping() {
    return needsMapping;
  }

  public void setNeedsMapping(boolean needsMapping) {
    this.needsMapping = needsMapping;
  }

  public RawTriple needsMapping(boolean needsMapping) {
    this.needsMapping = needsMapping;
    return this;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public RawTriple object(String object) {
    this.object = object;
    return this;
  }

  public String getRawText() {
    return rawText;
  }

  public void setRawText(String rawText) {
    this.rawText = rawText;
  }

  public RawTriple rawText(String rawText) {
    this.rawText = rawText;
    return this;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public RawTriple sourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
    return this;
  }

  public Long getExtractionTime() {
    return extractionTime;
  }

  public void setExtractionTime(Long extractionTime) {
    this.extractionTime = extractionTime;
  }

  public RawTriple extractionTime(Long extractionTime) {
    this.extractionTime = extractionTime;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public RawTriple version(String version) {
    this.version = version;
    return this;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public RawTriple accuracy(Double accuracy) {
    this.accuracy = accuracy;
    return this;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return "RawTriple{" +
        "module='" + module + '\'' +
        ", subject='" + subject + '\'' +
        ", predicate='" + predicate + '\'' +
        ", needsMapping=" + needsMapping +
        ", object='" + object + '\'' +
        ", rawText='" + rawText + '\'' +
        ", sourceUrl='" + sourceUrl + '\'' +
        ", extractionTime=" + extractionTime +
        ", version='" + version + '\'' +
        ", accuracy=" + accuracy +
        ", metadata=" + metadata +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RawTriple rawTriple = (RawTriple) o;

    if (needsMapping != rawTriple.needsMapping) return false;
    if (module != null ? !module.equals(rawTriple.module) : rawTriple.module != null) return false;
    if (subject != null ? !subject.equals(rawTriple.subject) : rawTriple.subject != null) return false;
    if (predicate != null ? !predicate.equals(rawTriple.predicate) : rawTriple.predicate != null) return false;
    if (object != null ? !object.equals(rawTriple.object) : rawTriple.object != null) return false;
    if (rawText != null ? !rawText.equals(rawTriple.rawText) : rawTriple.rawText != null) return false;
    if (sourceUrl != null ? !sourceUrl.equals(rawTriple.sourceUrl) : rawTriple.sourceUrl != null) return false;
    if (extractionTime != null ? !extractionTime.equals(rawTriple.extractionTime) : rawTriple.extractionTime != null)
      return false;
    if (version != null ? !version.equals(rawTriple.version) : rawTriple.version != null) return false;
    if (accuracy != null ? !accuracy.equals(rawTriple.accuracy) : rawTriple.accuracy != null) return false;
    return metadata != null ? metadata.equals(rawTriple.metadata) : rawTriple.metadata == null;
  }

  @Override
  public int hashCode() {
    int result = module != null ? module.hashCode() : 0;
    result = 31 * result + (subject != null ? subject.hashCode() : 0);
    result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
    result = 31 * result + (needsMapping ? 1 : 0);
    result = 31 * result + (object != null ? object.hashCode() : 0);
    result = 31 * result + (rawText != null ? rawText.hashCode() : 0);
    result = 31 * result + (sourceUrl != null ? sourceUrl.hashCode() : 0);
    result = 31 * result + (extractionTime != null ? extractionTime.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
    result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
    return result;
  }
}
