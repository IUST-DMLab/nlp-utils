/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;

import java.util.List;

public interface RawTripleExtractor {

  List<RawTriple> extract(String source, String version, String text);

  List<RawTriple> extract(String source, String version, List<List<ResolvedEntityToken>> text);
}
