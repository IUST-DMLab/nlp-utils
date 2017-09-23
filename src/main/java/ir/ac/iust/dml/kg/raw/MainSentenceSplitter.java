/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw;

import ir.ac.iust.nlp.jhazm.utility.RegexPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainSentenceSplitter {
    private static MainSentenceSplitter instance;
    private final RegexPattern pattern;

    public MainSentenceSplitter() {
        this.pattern = new RegexPattern("([!\\.\\?⸮؟\\:]+)[ \\n]+", "$1\n\n");
    }

    public static MainSentenceSplitter i() {
        if (instance != null) return instance;
        instance = new MainSentenceSplitter();
        return instance;
    }

    public List<String> tokenize(String text) {
        text = this.pattern.Apply(text);
        List<String> sentences = Arrays.asList(text.split("\n\n"));
        List<String> newSentences = new ArrayList<String>();
        for (String sentence : sentences) {
            sentence = sentence.replace("\n", " ").trim();
            if (sentence.length() > 2)
                newSentences.add(sentence);
        }
        return newSentences;
    }
}
