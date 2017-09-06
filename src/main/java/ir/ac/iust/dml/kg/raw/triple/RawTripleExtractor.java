package ir.ac.iust.dml.kg.raw.triple;

import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;

import java.util.List;

public interface RawTripleExtractor {

  List<RawTriple> extract(String source, String version, String text);

  List<RawTriple> extract(String source, String version, List<List<ResolvedEntityToken>> text);
}
