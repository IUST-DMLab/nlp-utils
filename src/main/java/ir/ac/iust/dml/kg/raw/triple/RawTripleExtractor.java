package ir.ac.iust.dml.kg.raw.triple;

import java.util.List;

public interface RawTripleExtractor {
  List<RawTriple> extract(String source, String version, String text);
}
