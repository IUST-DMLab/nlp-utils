package ir.ac.iust.dml.kg.raw;

import ir.ac.iust.dml.kg.raw.ner.NerTaggedWord;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class NamedEntityRecognizerTest {
  @Test
  public void summarize() throws IOException, InterruptedException {
    String input = "من مجید عسگری هستم. اگر با مجید کار داشتی به من زنگ بزن.";
    final List<List<NerTaggedWord>> nerTags = NamedEntityRecognizer.nerText(input, false);
    assert nerTags.get(0).get(1).getNerTag().equals("B-PERS");
    assert nerTags.get(0).get(2).getNerTag().equals("I-PERS");
    final List<List<NerTaggedWord>> nerTags2 = NamedEntityRecognizer.nerText(input, true);
    assert nerTags2.get(1).get(2).getNerTag().equals("B-PERS");
  }
}
