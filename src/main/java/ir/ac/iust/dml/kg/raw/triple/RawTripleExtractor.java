package ir.ac.iust.dml.kg.raw.triple;

import java.util.List;

public interface RawTripleExtractor {
  public List<RawTriple> generate(String text);
}
