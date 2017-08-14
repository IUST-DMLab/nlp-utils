package ir.ac.iust.dml.kg.raw.extractor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class ResolvedEntityTokenResource {
  private String iri;
  private boolean isResource;
  private String mainClass;
  private Set<String> classes = new HashSet<>();

  public String getIri() {
    return iri;
  }

  void setIri(String iri) {
    this.iri = iri;
  }

  public boolean isResource() {
    return isResource;
  }

  void setResource(boolean resource) {
    isResource = resource;
  }

  public String getMainClass() {
    return mainClass;
  }

  void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public Set<String> getClasses() {
    return classes;
  }

  void setClasses(Set<String> classes) {
    this.classes = classes;
  }
}
