package ir.ac.iust.dml.kg.raw.triple;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RawTripleTester {
  @Test
  public void writeRawTriple() throws IOException {
    final String MODULE = "test";
    final String URL = "mongo://Corpus/";
    final long TIME = System.currentTimeMillis();
    final String VERSION = "2.3";
    final RawTripleBuilder builder = new RawTripleBuilder(MODULE, URL, TIME, VERSION);
    final Path PATH = Paths.get(System.getProperty("user.home"), "test.json");
    final RawTripleExporter exporter = new RawTripleExporter(PATH);
    final RawTriple triple1 = builder.create()
        .subject("http://test.org/s1").predicate("http://test.org/p1")
        .object("http://test.org/o1").rawText("Sample Text 1")
        .accuracy(0.1).needsMapping(true);
    exporter.write(triple1);
    final RawTriple triple2 = builder.create()
        .subject("http://test.org/s2").predicate("http://test.org/p2")
        .object("http://test.org/o2").rawText("Sample Text 2")
        .accuracy(0.1).needsMapping(true);
    exporter.write(triple2);
    exporter.close();

    final RawTripleImporter importer = new RawTripleImporter(PATH);
    final RawTriple first = importer.next();
    assert first.getModule().equals(MODULE);
    assert first.getSourceUrl().equals(URL);
    assert first.getExtractionTime().equals(TIME);
    assert first.getVersion().equals(VERSION);
    assert first.equals(triple1);
    final RawTriple second = importer.next();
    assert second.getModule().equals(MODULE);
    assert second.getSourceUrl().equals(URL);
    assert second.getExtractionTime().equals(TIME);
    assert second.getVersion().equals(VERSION);
    assert second.equals(triple2);
    importer.close();
  }
}
