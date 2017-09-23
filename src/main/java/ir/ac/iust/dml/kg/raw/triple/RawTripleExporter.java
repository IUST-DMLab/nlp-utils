/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings(value = {"unused"})
public class RawTripleExporter implements Closeable {
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final Writer out;
  private boolean first = false;

  public RawTripleExporter(Path path) throws IOException {
    this.out = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(path.toFile()), "UTF-8"));
    this.out.write("[");
  }

  public void write(RawTriple triple) throws IOException {
    if (!first) first = true;
    else this.out.write(",");
    this.out.write("\n");
    this.out.write(gson.toJson(triple));
  }

  public void writeTripleList(List<RawTriple> rawTripleList) {
    for (RawTriple rawTriple : rawTripleList) {
      try {
        write(rawTriple);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      this.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    this.out.write("\n]");
    this.out.close();
  }
}
