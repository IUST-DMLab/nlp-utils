package ir.ac.iust.dml.kg.raw.triple;

import java.util.HashMap;
import java.util.Map;

/**
 * create by Majid Asgari
 * 96/04/22
 * Standard triple for raw data
 */
@SuppressWarnings(value = {"unused", "WeakerAccess"})
public class RawTriple {
  private String module;
  private String subject;
  private String predicate;
  private String object;
  private String rawText;
  private String sourceUrl;
  private Long extractionTime;
  private String version;
  private Long accuracy;
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

  public Long getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Long accuracy) {
    this.accuracy = accuracy;
  }

  public RawTriple accuracy(Long accuracy) {
    this.accuracy = accuracy;
    return this;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }
}
