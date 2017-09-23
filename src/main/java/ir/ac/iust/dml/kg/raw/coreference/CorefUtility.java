/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.coreference;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CorefUtility {

    /*  public List<CorefEntity> getSentenceEntities(CoreMap coreMap)
      {
          List<CoreLabel> coreLabels=coreMap.get(CoreAnnotations.TokensAnnotation.class);
          List<CorefEntity> corefEntities=new ArrayList<CorefEntity>();
          for(CoreLabel coreLabel:coreLabels)
          {
              if(coreLabel.tag().equals("PRO") && coreLabel.word().matches("وی|او"))
              {
                  CorefEntity corefEntity=new CorefEntity();
               //   corefEntity.setEntityTokens(coreLabel);
              }
          }
      }*/
    public List<CoreMap> getSentences(List<CoreLabel> coreLabels) {
        List<String> sentenceDelimiterSet=new ArrayList<String>();
        sentenceDelimiterSet.add(".");
        sentenceDelimiterSet.add("?");
        sentenceDelimiterSet.add("!");
        sentenceDelimiterSet.add(":");
        sentenceDelimiterSet.add(".");
        List<CoreMap> sentences = new ArrayList<CoreMap>();
        CoreMap sentence = new ArrayCoreMap();
        List<CoreLabel> tokens = new ArrayList<CoreLabel>();
        ListIterator wordsIterator = coreLabels.listIterator();
        while (wordsIterator.hasNext()) {
            CoreLabel currentWord = (CoreLabel) wordsIterator.next();
            tokens.add(currentWord);
            if (sentenceDelimiterSet.contains(currentWord.word())) {
                sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
                sentences.add(sentence);
                tokens = new ArrayList<CoreLabel>();
                sentence = new ArrayCoreMap();
            } else if (currentWord.tag() != null && currentWord.tag().equals("V") && wordsIterator.hasNext()) {
                CoreLabel nextWord = (CoreLabel) wordsIterator.next();
                if (nextWord.tag().contains("CON")) {
                    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
                    tokens = new ArrayList<CoreLabel>();
                    tokens.add(nextWord);
                    sentence = new ArrayCoreMap();
                } else {
                    wordsIterator.previous();
                }
            }
        }

        if (tokens.size() != 0) {
            sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
            sentences.add(sentence);
        }

        return sentences;
    }

    public static List<String> readListedFile(Class<?> classs, String fileName)  {
        List<String> outputList = new ArrayList<String>();

        InputStream in = readFileAsStream(classs, fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String line;
        try {
            while (null != (line = reader.readLine())) {
                outputList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputList;
    }

    public static InputStream readFileAsStream(Class<?> classs, String fileName)  {
        InputStream in=null;


        URL inputURL = classs.getResource(fileName);
        URLConnection conn = null;
        try {
            conn = inputURL.openConnection();
            in = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return in;
    }

    public List<String> readLines(String filePath){
        List<String> lines = new ArrayList<String>();
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if(strLine.length()>1)
                    lines.add(strLine.replace("  "," "));

            }
        } catch(Exception ex) {

        }
        return  lines;

    }

    public  static List<Mention> getMentions(List<CoreLabel> coreLabels,int index) {
        List<Mention> mentions=new ArrayList<Mention>();
        for(CoreLabel coreLabel:coreLabels) {
            if(coreLabel.tag().matches("PRO?") && coreLabel.word().matches("وی|او")) {
                Mention mention=new Mention();
                mention.setMofrad(true);
                mention.setMentionCoreLabel(coreLabel);
                mention.setNumber(3);
                mention.setPosTag(coreLabel.tag());
                mention.setType("PERS");
                mention.setIndex(index);
                mentions.add(mention);
            }
        }
        return mentions;
    }

    public static List<ReferenceEntity> getReferenceEntities(List<CoreLabel> coreLabels,int index) {
        List<ReferenceEntity> entityList = new ArrayList<ReferenceEntity>();
        CoreLabel currentCoreLabel;
        ListIterator<CoreLabel> coreLabelIterator = coreLabels.listIterator();
        CoreLabel nextCoreLabel = null;
        List<CoreLabel> entityCoreLabelList;
        while (coreLabelIterator.hasNext()) {

            if (nextCoreLabel == null)
                currentCoreLabel = coreLabelIterator.next();
            else {
                currentCoreLabel = nextCoreLabel;
                nextCoreLabel = null;
            }

            if (currentCoreLabel.word().isEmpty() || currentCoreLabel.word().length() < 2)
                continue;

            if (!currentCoreLabel.ner().startsWith("O")) {
                entityCoreLabelList = new ArrayList<CoreLabel>();
                ReferenceEntity entity = new ReferenceEntity();

                entityCoreLabelList.add(currentCoreLabel);
                entity.setType(currentCoreLabel.ner().substring(2));
                entity.setIndex(index);
                if (coreLabelIterator.hasNext()) {
                    nextCoreLabel = coreLabelIterator.next();
                    while (nextCoreLabel.ner().startsWith("I_")) {
                        entityCoreLabelList.add(nextCoreLabel);
                        if (coreLabelIterator.hasNext())
                            nextCoreLabel = coreLabelIterator.next();
                        else
                            break;
                    }
                }
                entity.setEntityTokens(entityCoreLabelList);
                entityList.add(entity);
            }
        }
        return entityList;
    }

}
