/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw;

import ir.ac.iust.nlp.jhazm.utility.RegexPattern;
import ir.ac.iust.nlp.jhazm.utils.FileHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;


public class MainWordTokenizer {

    private static WordTokenizer instance;
    private List<String> verbs;
    private boolean joinVerbParts = true;
    private HashSet<String> beforeVerbs;
    private HashSet<String> afterVerbs;
    private RegexPattern pattern;

    public MainWordTokenizer() throws IOException {
        this(true);
    }

    public MainWordTokenizer(boolean joinVerbParts) throws IOException {
        this("data/verbs.dat", joinVerbParts);
    }

    public MainWordTokenizer(String verbsFile) throws IOException {
        this(verbsFile, true);
    }

    public MainWordTokenizer(String verbsFile, boolean joinVerbParts)
        throws IOException {
        this.joinVerbParts = joinVerbParts;
        this.pattern = new RegexPattern("([؟!\\?]+|[:\\.،؛»\\]\\)\\}\"«\\[\\(\\{])", " $1 ");

        if (this.joinVerbParts) {
            String[] tokens;

            tokens = new String[]{
                "ام", "ای", "است", "ایم", "اید", "اند",
                "بودم", "بودی", "بود", "بودیم", "بودید", "بودند",
                "باشم", "باشی", "باشد", "باشیم", "باشید", "باشند",
                "شده ام", "شده ای", "شده است", "شده ایم", "شده اید", "شده اند",
                "شده بودم", "شده بودی", "شده بود", "شده بودیم", "شده بودید", "شده بودند",
                "شده باشم", "شده باشی", "شده باشد", "شده باشیم", "شده باشید", "شده باشند",
                "نشده ام", "نشده ای", "نشده است", "نشده ایم", "نشده اید", "نشده اند",
                "نشده بودم", "نشده بودی", "نشده بود", "نشده بودیم", "نشده بودید", "نشده بودند",
                "نشده باشم", "نشده باشی", "نشده باشد", "نشده باشیم", "نشده باشید", "نشده باشند",
                "شوم", "شوی", "شود", "شویم", "شوید", "شوند",
                "شدم", "شدی", "شد", "شدیم", "شدید", "شدند",
                "نشوم", "نشوی", "نشود", "نشویم", "نشوید", "نشوند",
                "نشدم", "نشدی", "نشد", "نشدیم", "نشدید", "نشدند",
                "می‌شوم", "می‌شوی", "می‌شود", "می‌شویم", "می‌شوید", "می‌شوند",
                "می‌شدم", "می‌شدی", "می‌شد", "می‌شدیم", "می‌شدید", "می‌شدند",
                "نمی‌شوم", "نمی‌شوی", "نمی‌شود", "نمی‌شویم", "نمی‌شوید", "نمی‌شوند",
                "نمی‌شدم", "نمی‌شدی", "نمی‌شد", "نمی‌شدیم", "نمی‌شدید", "نمی‌شدند",
                "خواهم شد", "خواهی شد", "خواهد شد", "خواهیم شد", "خواهید شد", "خواهند شد",
                "نخواهم شد", "نخواهی شد", "نخواهد شد", "نخواهیم شد", "نخواهید شد", "نخواهند شد"
            };

            this.afterVerbs = new HashSet<>(Arrays.asList(tokens));

            tokens = new String[]{
                "خواهم", "خواهی", "خواهد", "خواهیم", "خواهید", "خواهند",
                "نخواهم", "نخواهی", "نخواهد", "نخواهیم", "نخواهید", "نخواهند",
                "نداشته", "داشته"
            };

            this.beforeVerbs = new HashSet<>(Arrays.asList(tokens));

            this.verbs = new ArrayList<>(Files.readAllLines(FileHandler.getPath(verbsFile), Charset.forName("UTF8")));
            Collections.reverse(this.verbs);
            for (int i = 0; i < this.verbs.size(); i++) {
                String verb = this.verbs.get(i);
                this.verbs.set(i, verb.trim().split("#")[0] + "ه");
            }
        }
    }

    public static WordTokenizer i() throws IOException {
        if (instance != null) return instance;
        instance = new WordTokenizer();
        return instance;
    }


    HashSet<String> getBeforeVerbs() {
        return beforeVerbs;
    }

    HashSet<String> getAfterVerbs() {
        return afterVerbs;
    }

    List<String> getVerbs() {
        return verbs;
    }

    public List<String> tokenize(String sentence) {
        sentence = this.pattern.Apply(sentence).trim().replace("  ", " ");
        List<String> tokens = Arrays.asList(sentence.split(" +"));
        //tokens = filterEmptyTokens(tokens);
        if (this.joinVerbParts)
            tokens = this.JoinVerbParts(tokens);
        return tokens;
    }

    private List<String> filterEmptyTokens(List<String> tokens) {

        List<String> newTokenList = new ArrayList<String>();

        for (String token : tokens) {
            if (token.toCharArray()[0] != 8204) {
                newTokenList.add(token);
            }
        }

   /*     Iterator<String> tokenIter = tokens.iterator();
        while (tokenIter.hasNext()) {
            String token = tokenIter.next();
          if( token.toCharArray()[0]==8204)
              tokenIter.remove();
        }*/

        return newTokenList;
    }

    private List<String> JoinVerbParts(List<String> tokens) {
        Collections.reverse(tokens);
        List<String> newTokens = new ArrayList<>();

        for (String token : tokens) {
            if (newTokens.size() > 0) {
                String lastWord = newTokens.get(newTokens.size() - 1);
                if (this.beforeVerbs.contains(token) ||
                    (this.afterVerbs.contains(lastWord) && this.verbs.contains(token))) {
                    lastWord = token + " " + lastWord;
                    newTokens.set(newTokens.size() - 1, lastWord);
                } else
                    newTokens.add(token);
            } else
                newTokens.add(token);
        }

        Collections.reverse(newTokens);
        return newTokens;
    }
}


