package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.TaggedWord;
import org.junit.Test;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tester {

    @Test
    public void normalizer() {
        final String input = "سلام من مجید عسکري هستم.";
        final String expected = "سلام من مجید عسکری هستم.";
        assert Objects.equals(expected, Normalizer.normalize(input));
    }

    @Test
    public void normalizerBrackets() {
      final String input = "مجید عسگری (زاده ۲۸ اسفند ۶۳) در تهران" +
          " [محله جیحون] -که پایتخت ایران بود- بدنیا آمد اما نتیجه ۲-۱ به نفع رم رقم خورد." +
          "بازی بعدی ولی، ۳-۲ شد.";
      final String expected = "مجید عسگری در تهران  بدنیا آمد اما نتیجه ۲-۱ به نفع رم رقم خورد.بازی بعدی ولی، ۳-۲ شد.";
        assert Objects.equals(expected, Normalizer.removeBrackets(input));
    }

    @Test
    public void sentenceTokenizer() {
        final String input = "سلام من مجید عسکري هستم.";
        final String expected = "سلام من مجید عسکری هستم.";
        assert Objects.equals(expected, SentenceTokenizer.SentenceSplitterRaw(input).get(0));
    }

    @Test
    public void wordTokenizer() {
        final String input = "سلام من مجید عسکري هستم.";
        final List<String> expected = Arrays.asList("سلام", "من", "مجید", "عسکری", "هستم", ".");
        assert Objects.equals(expected, WordTokenizer.tokenizeRaw(input).get(0));
    }

    @Test
    public void wordTokenizerTest() {
        final String input = "سلام به او برسان\n" +
                "\n" +
                "\n" +
                "\n" +
                "من او را دوست دارم \n" +
                "\n" +
                "\n" +
                "\n";
        final List<String> expected = Arrays.asList("سلام", "من", "مجید", "عسکری", "هستم", ".");
        List<String> strings = WordTokenizer.tokenize(input);
        System.out.println(strings);
    }

    @Test
    public void sentenceSplitterTest() {
        final String input = "وی گفت: سلام من را به او برسان.";
        final List<String> expected = Arrays.asList("سلام", "من", "مجید", "عسکری", "هستم", ".");
        List<String> strings = SentenceTokenizer.SentenceSplitterRaw(input);
        System.out.println(strings);
    }


    @Test
    public void posTag() {
        final String input = "سلام من مجید عسکري هستم.";
        // just printing :-)
        final List<List<TaggedWord>> tagged = POSTagger.tagRaw(input);
        tagged.forEach(System.out::println);

        // real test
        final List<TaggedWord> expected = Arrays.asList(
                new TaggedWord("سلام", "NE"),
                new TaggedWord("من", "PRO"),
                new TaggedWord("مجید", "N"),
                new TaggedWord("عسکری", "N"),
                new TaggedWord("هستم", "V"),
                new TaggedWord(".", "PUNC"));
        assert Objects.equals(expected, POSTagger.tagRaw(input).get(0));
    }

    @Test
    public void dependencyParse() {
        final String input = "سلام من مجید عسکري هستم.";
        // just printing :-)
        final List<ConcurrentDependencyGraph> tagged = DependencyParser.parseRaw(input);
        tagged.forEach(System.out::println);

        //real test
        final ConcurrentDependencyGraph graph = tagged.get(0);
        final String DEP_REL_LABEL = "DEPREL";
        ConcurrentDependencyNode node = graph.getDependencyNode(1);
        assert node.getHeadIndex() == 5 && node.getLabel(DEP_REL_LABEL).equals("SBJ");
        node = graph.getDependencyNode(2);
        assert node.getHeadIndex() == 1 && node.getLabel(DEP_REL_LABEL).equals("MOZ");
        node = graph.getDependencyNode(3);
        assert node.getHeadIndex() == 5 && node.getLabel(DEP_REL_LABEL).equals("MOS");
        node = graph.getDependencyNode(4);
        assert node.getHeadIndex() == 3 && node.getLabel(DEP_REL_LABEL).equals("MOZ");
        node = graph.getDependencyNode(5);
        assert node.getHeadIndex() == 0 && node.getLabel(DEP_REL_LABEL).equals("ROOT");
        node = graph.getDependencyNode(6);
        assert node.getHeadIndex() == 5 && node.getLabel(DEP_REL_LABEL).equals("PUNC");
    }


}
