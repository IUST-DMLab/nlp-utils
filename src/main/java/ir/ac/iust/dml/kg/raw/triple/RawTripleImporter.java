/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;

@SuppressWarnings(value = {"unused"})
public class RawTripleImporter implements Closeable, Iterator<RawTriple> {

  private final Gson gson = new Gson();
  private final BufferedReader reader;
  private RawTriple lastTriples;

  public RawTripleImporter(Path path) throws IOException {
    this(new FileInputStream(path.toFile()));
  }

  public RawTripleImporter(InputStream stream) throws IOException {
    this.reader = new BufferedReader((new InputStreamReader(stream, "UTF8")));
    reader.readLine();
    lastTriples = extractNext();
  }

  private RawTriple extractNext() throws IOException {
    int numberOfOpenBraces = 0;
    final StringBuilder buffer = new StringBuilder();
    while (true) {
      String line = reader.readLine();
      if (line == null) break;
      line = line.trim();
      if (line.contains("{")) numberOfOpenBraces++;
      buffer.append(line);
      if (line.contains("}")) {
        numberOfOpenBraces--;
        if (numberOfOpenBraces == 0) break;
      }
    }
    if (buffer.length() == 0) return null;
    if (buffer.charAt(buffer.length() - 1) == ',') buffer.setLength(buffer.length() - 1);
    return buffer.length() == 0 ? null : gson.fromJson(buffer.toString(), RawTriple.class);
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public boolean hasNext() {
    return lastTriples != null;
  }

  @Override
  public RawTriple next() {
    final RawTriple oldLast = lastTriples;
    try {
      lastTriples = extractNext();
    } catch (Exception e) {
      lastTriples = null;
    }
    return oldLast;
  }
}
