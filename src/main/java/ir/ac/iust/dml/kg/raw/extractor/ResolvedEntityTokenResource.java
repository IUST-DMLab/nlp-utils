/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.extractor;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class ResolvedEntityTokenResource implements Comparable<ResolvedEntityTokenResource> {
  private String iri;
  private boolean isResource;
  private float rank;
  private String mainClass;
  private Set<String> classes = new HashSet<>();


  public ResolvedEntityTokenResource copy() {
    ResolvedEntityTokenResource copy = new ResolvedEntityTokenResource();
    copy.iri = this.iri;
    copy.isResource = this.isResource;
    copy.rank = this.rank;
    copy.mainClass = this.mainClass;
    copy.classes.addAll(this.classes);
    return copy;
  }

  public String getIri() {
    return iri;
  }

  void setIri(String iri) {
    this.iri = iri;
  }

  public float getRank() {
    return rank;
  }

  public void setRank(float rank) {
    if (rank > 1) this.rank = 1;
    else this.rank = rank;
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

  @Override
  public int hashCode() {
    int result = iri != null ? iri.hashCode() : 0;
    result = 31 * result + (isResource ? 1 : 0);
    result = 31 * result + (mainClass != null ? mainClass.hashCode() : 0);
    result = 31 * result + (classes != null ? classes.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResolvedEntityTokenResource that = (ResolvedEntityTokenResource) o;

    if (isResource != that.isResource) return false;
    if (iri != null ? !iri.equals(that.iri) : that.iri != null) return false;
    if (mainClass != null ? !mainClass.equals(that.mainClass) : that.mainClass != null) return false;
    return classes != null ? classes.equals(that.classes) : that.classes == null;
  }

  /**
   * sorts descending
   */
  @Override
  public int compareTo(@NotNull ResolvedEntityTokenResource o) {
    return Float.compare(o.rank, rank);
  }
}
