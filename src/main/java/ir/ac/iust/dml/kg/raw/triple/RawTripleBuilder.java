/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

@SuppressWarnings(value = {"unused", "WeakerAccess"})
public class RawTripleBuilder {
  private String module;
  private String sourceUrl;
  private Long extractionTime;
  private String version;
  private boolean needsMapping;

  public RawTripleBuilder() {
  }

  public RawTripleBuilder(String module) {
    this.module = module;
  }

  public RawTripleBuilder(String module, String sourceUrl) {
    this.module = module;
    this.sourceUrl = sourceUrl;
  }

  public RawTripleBuilder(String module, String sourceUrl, String version) {
    this.module = module;
    this.sourceUrl = sourceUrl;
    this.version = version;
  }

  public RawTripleBuilder(String module, String sourceUrl, Long extractionTime, String version) {
    this.module = module;
    this.sourceUrl = sourceUrl;
    this.extractionTime = extractionTime;
    this.version = version;
  }

  public RawTripleBuilder(String module, String sourceUrl, Long extractionTime, String version, boolean needsMapping) {
    this.module = module;
    this.sourceUrl = sourceUrl;
    this.extractionTime = extractionTime;
    this.version = version;
    this.needsMapping = needsMapping;
  }

  public RawTriple create() {
    final RawTriple triple = new RawTriple();
    triple.setModule(module);
    triple.setSourceUrl(sourceUrl);
    triple.setExtractionTime(extractionTime);
    triple.setVersion(version);
    return triple;
  }


  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public Long getExtractionTime() {
    return extractionTime;
  }

  public void setExtractionTime(Long extractionTime) {
    this.extractionTime = extractionTime;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
