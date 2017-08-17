package ir.ac.iust.dml.kg.raw;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import ir.ac.iust.dml.kg.raw.coreference.*;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 2/3/17 4:35 PM
 */
public class CorefTest {

    private static Configuration config;

    static {
        try {
            config = new PropertiesConfiguration("config.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void main1(String[] args) {

        CorefUtility corefUtility = new CorefUtility();
        List<CoreLabel> coreLabelList = new ArrayList<CoreLabel>();
        coreLabelList = readTokens(config.getString("testFilePath"));
        Annotation annotation = new Annotation();
        annotation.set(CoreAnnotations.TokensAnnotation.class, coreLabelList);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, corefUtility.getSentences(coreLabelList));

    }

    @Test
    public void testCorefWithMentionReplace() {
        String testString = "مرتضی پاشایی از دوران کودکی همیشه نسبت به موسیقی حس خاص و مبهمی داشت. او از سال ۱۳۸۹ با انتشار اینترنتیِ قطعات خود شروع به کار کرد. او با قطعهٔ «یکی هست» به شهرت رسید. کارشناسان، او را از پیروان سبک محسن یگانه و شادمهر عقیلی می‌دانند.";
        ReferenceFinder rfinder = new ReferenceFinder();
        String outputText = rfinder.getAnnotationTextAfterCoref(testString);
        System.out.println(outputText);

    }

    public static void main(String[] args) {

        String testString = "بذرپاش تاکید کرد: بخش عمده‌ای از بانک‌هایی که در کشور حضور دارند تنها و تنها برای تامین اعتبار پروژه‌های یک سری افراد خاص، از جیب مردم شکل گرفته‌اند و در این راه وفادارانه به این افراد خدمت می‌کنند.\n" +
                "\n" +
                "به گزارش حوزه احزاب خبرگزاری فارس، مهرداد بذرپاش عضو هیات‌رئیسه مجلس نهم که در جمع دانشجویان دانشگاه شهید چمران اهواز سخنرانی می‌کرد اظهار داشت: خوزستان، مهد پرورش 21 هزار شهیدی است که خود، پذیرای شهیدانی از سایر مناطق کشور بودند. شهدای خوزستان از غریب ترین شهدا هستند، همانطور که مردم این استان - که همیشه آماج تیرهای بلا بوده اند- یکی از مظلوم ترین های این مملکت هستند. دوران جنگ هشت‌ساله‌ دفاع مقدس، داستان ایستادگی خانواده های خوزستانی است. یکی از مهمترین ویژگی های مردم این خطه که آنها را متمایز از سایر بخش های کشور کرده است، میزبانی و مراقبت از خانواده رزمنده هایی بود که به واسطه جنگ به این استان آمده بودند.\n" +
                "\n" +
                "معاون رئیس جمهور سابق گفت: دولت باید خدمتگزار باشد نه منت‌گذار. وی ادامه داد: محتمل ترین سناریو برای آینده انقلاب اسلامی، فتح نرم افزاری جهان فکرها و اندیشه هاست. انقلاب اسلامی آزمون حضور در محضر مهدوی است؛ آنهایی که در این آزمون شکست خوردند در انقلاب مهدوی چگونه تاب خواهند آورد؟\n" +
                "\n" +
                "بذرپاش اضافه کرد: عدالت، اتهام و مچ گیری و توهین و تهدید نیست؛ علم هم مدرک و از خودبیگانگی و بی هویتی نیست. بذرپاش هم چنین تاکید کرد: نه عدالت با توییت کردن و تهدید کردن و بالانشستن بدست می آید نه علم با قدم زدن دیپلماتیک و یقه سفید پوشیدن؛ علم، از دل مغز جوان بی دغدغه اما امیدوار به مسیر متعالی بدست می آید و عدالت، از دل دست های پینه بسته جوان و پیری که روزانه بیست ساعت کار و جهاد می کنند.\n" +
                "\n" +
                "عضو سابق هیئت رئیسه مجلس متذکر شد: آینده انقلاب در اجتماعی و همگانی شدن عدالت و علم است و اگر سیاسیون دلشان برای آینده انقلاب می سوزد بجای لابی در پستو ها و احزاب دوسه نفره، با مردم باشند. وی ادامه داد: دنیا بیشتر از این، نام خمینی و فرزندانش را خواهد شنید؛ مهم نیست چند صباحی تعدادی غریبه لباس آشنا تن کنند و برای آرزوهای بچه گانه خود بهانه گیری کنند.\n" +
                "\n" +
                "وی در ادامه با اشاره به وضعیت اقتصادی و معیشتی مردم در این استان گفت: در حال حاضر این استان مستعد و بهره مند تنها 2 درصد از کارگاه های صنعتی 10 نفر شاغل به بالا را در کشور دارد. این تعداد کارگاه صنعتی در این استان نه در شأن این استان و نه در شأن انقلاب اسلامی است.\n" +
                "\n" +
                "بذرپاش ادامه داد: این استان که با مصرف 12 درصدی از مصرف برق کشور در رتبه دوم قرار دارد، در هفته های اخیر با عدم مدیریت صحیح، دچار بحران شد که جای بسی تامل دارد. وی افزود: این استان که خود دارای سدهای بزرگی است، در بخش روستایی در حدود 40 درصد روستاهای آن در شرایط بحرانی قرار دارند و در طول شبانه روز کمتر از 6 ساعت آب در برخی از روستاها وجود دارد. این آب شیرینی که برخی مردم را به آن حواله می دهند، کجا است. چرا فقط در شمال شهر تهران وجود دارد؟ این مردم روستایی که در جنگ، با زن و فرزند در مقابل دشمن ایستادند و زیر بمب ها و موشک های تهمت و افترا جنگیدند، تحت هیچ شبکه فاضلابی نیستند.\n" +
                "\n" +
                "بذرپاش همچنین در ادامه صحبت های خود با اشاره به وضعیت زیرساختی این استان گفت: آیا جاده ها و مسیرهای این استان که به گفته خود مسئولین در رتبه 12 ام قرار دارد، پاسخی درخور به رشادت ها و از خودگذشتگی های مردمان این استان بوده است؟ آیا با این سیستم زیرساختی می توان این استان را توسعه داد؟ وی گفت: وضعیت سیستم بانکی در این استان به حدی منفعت گرا است که در رتبه آخر پرداخت تسهیلات از محل سپرده های جذب شده از جیب مردم، قرار دارد.\n" +
                "\n" +
                "عضو سابق هیئت رئیسه مجلس با بیان وضعیت رفاه اجتماعی مردم خوزستان نیز بیان داشت: متاسفانه این استان در حال حاضر در رتبه اول تعداد خانواده های مستمری بگیر تحت پوشش بهزیستی قرار دارد که مایه شرمساری است. وی تاکید کرد: نتیجه این نظام سلامتی که برخی از مسئولین با کنایه در همایشی می فرمایند\" مردم را به آینده نظام و کشور مایوس نکنید\"؛ برای مردم این استان تنها 16 تخت به ازای هر ده هزار نفر جمعیت شده است و یا سرانه مکان های ورزشی این استان در کشور در جایگاه نوزدهم قرار دارد.\n" +
                "\n" +
                "وی گفت: دیپلماسی سبز فعال، صرفا شرکت در نشست های بین المللی نیست بلکه تقویت دیپلماسی زیست محیطی و پایبندی به آن، در راستای هم افزایی منطقه ای برای حل مشکلات و بحرانهای به وجود آمده باید اولویت دیپلماسی زیست محیطی کشور باشد.\n" +
                "\n" +
                "بذرپاش ضمن تاکید بر خسارت های ناشی از آلودگی هوا در کشور گفت: آنهایی که ادعای کار کارشناسی و خبرگی در کشور دارند، چرا نمی فرمایند در سالهای اخیر در حدود 600 میلیون دلار خسارت های وارد بر کشور ناشی از مرگ و میرها بوده است و در حدود 25 میلیون دلار خسارت های ناشی از بیماری متاثر از موضوع آلودگی هوا را با سوء مدیریت های خود به کشور تحمیل کرده اند.\n" +
                "\n" +
                "بذرپاش ادامه داد: شما خودتان مانع هستید وگرنه مردم با همت و با غیرت خوزستان، خودشان اهل کار و مجاهدت هستند و نیازی به وعدهای شما ندارند. وی تاکید داشت: خوزستان را باید با تخصیص درست منابع و توسعه مکانیزاسیون در فعالیت های کشاورزی ساخت. خوزستان را با توسعه درست زیرساخت ها و بهبود شبکه حمل و نقل آن می توان ساخت نه با کنایه پراکنی.\n" +
                "\n" +
                "نماینده سابق مجلس با تشریح ابعاد و الزامات سرمایه گذاری توسعه محور در کشور گفت: تنها راه نجات کشور و رفع بحران و حل مشکلات ناشی از آن از قبیل بیکاری، فراهم کردن بسترهای سرمایه گذاری بخش خصوصی و عدم دخالت دولت ها با عناوین مختلف در آن است. وی ادامه داد: وقتی سرمایه گذاری وارد یک فعالیت اقتصادی می شود، اول باید فاتحه داراییش را بخواند چون اینقدر به بهانه های مختلف از آن پول و عوارض و مالیات دریافت می کنند که ترجیح می دهد که پولش را در اختیار بانک ها قرار دهد تا آنها هم به هر کاری به جز بانکداری با آن پول بپردازند.\n" +
                "\n" +
                "بذرپاش تاکید کرد: بخش عمده ای از بانک هایی که در کشور حضور دارند تنها و تنها برای تامین اعتبار پروژه های یک سری افراد خاص، از جیب مردم شکل گرفته اند و در این راه وفادارانه به این افراد خدمت می کنند.\n" +
                "\n" +
                "نماینده سابق مجلس شورای اسلامی گفت: بجای صدور مجوز از برج های 74 متری در مرکز برای شکل گیری بانک ها و موسسات اعتباری در پایتخت، به استان ها مجوز دهید تا بانک خودشان را داشته باشند و از سرمایه خود مردم هر استان بتوان برای همان استان بهره برداری کرد نه پول مردم استان ها را جمع کنند و در اختیار سرمایه گذاران پایتخت نشین قرار دهند.";

        testString = "«الیاس حضرتی»  مهمان برنامه دست خط بودند که در ادامه مشروح گفت‌وگوی وی ازنظرتان می‌گذرد. او نماینده مردم تهران در مجلس شورای اسلامی است.";
      testString="\"«الیاس حضرتی»  مهمان برنامه دست خط بودند که در ادامه مشروح گفت‌وگوی وی ازنظرتان می‌گذرد. او نماینده مردم تهران در مجلس شورای اسلامی است.\"";
        testString="مرتضی پاشایی از دوران کودکی همیشه نسبت به موسیقی حس خاص و مبهمی داشت. او از سال ۱۳۸۹ با انتشار اینترنتیِ قطعات خود شروع به کار کرد. او با قطعهٔ «یکی هست» به شهرت رسید. کارشناسان، او را از پیروان سبک محسن یگانه و شادمهر عقیلی می‌دانند.";

        Annotation annotation = new Annotation(testString);

        TextProcess tp = new TextProcess();
        tp.preProcess(annotation);
        tp.annotateNamedEntity(annotation);
        //  tp.dependecyParser(annotation);
/*
        List<CoreLabel> coreLabels = annotation.get(CoreAnnotations.TokensAnnotation.class);
        QuotationExtractor quotationExtractor = new QuotationExtractor();
        List<QuotationBound> quotationBounds = quotationExtractor.applyQuotationRules(annotation);
      */
        ReferenceFinder rfinder = new ReferenceFinder();
        List<ir.ac.iust.dml.kg.raw.coreference.CorefChain> corefChains = rfinder.annotateCoreference(annotation);
        for (CorefChain corefChain : corefChains) {

            System.out.println(corefChain.toString());
        }
      // evaluateCoref();
    }

    @Test
    public void evaluateCoref() {
        String filePath = config.getString("testFileInput");
        FileInputStream fstream = null;
        Annotation annotation = new Annotation("");
        try {
            fstream = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            List<CoreLabel> sentenceCoreLabels = new ArrayList<CoreLabel>();
            List<CoreLabel> allCoreLabels = new ArrayList<CoreLabel>();
            List<CoreMap> coreMaps = new ArrayList<CoreMap>();
            boolean isContinuosNer = false;
            boolean isContinuosCoref = false;
            String referenceNumber = "";
            String mentionOrReference = "";
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() > 0) {
                    String[] strs = strLine.split("\t");

                    CoreLabel coreLabel = new CoreLabel();
                    coreLabel.setWord(strs[3]);
                    coreLabel.setTag(strs[5]);
                    coreLabel.setNER("O");
                    coreLabel.setIndex(Integer.parseInt(strs[2]));
                    if (strs[9].equals("Person")) {
                        coreLabel.setNER("B_PERS");
                        isContinuosNer = false;
                    } else if (strs[9].equals("Person(*")) {
                        coreLabel.setNER("B_PERS");
                        isContinuosNer = true;
                    } else if (strs[9].matches("\\*\\)") && isContinuosNer) {
                        coreLabel.setNER("I_PERS");
                        isContinuosNer = false;
                    } else if (strs[9].matches("\\*") && isContinuosNer) {
                        coreLabel.setNER("I_PERS");
                        isContinuosNer = true;
                    }
                    if (coreLabel.word().matches("وی|او") || coreLabel.ner().contains("PERS")) {
                        String referenceStr = "";
                        if (coreLabel.word().matches("وی|او")) {
                            mentionOrReference = "_Men";
                            referenceStr = strs[11];

                        } else if (!coreLabel.tag().equals("PRO")) {
                            mentionOrReference = "_Ref";
                            referenceStr = strs[10];
                        }

                        if (referenceStr.matches("\\d+")) {
                            coreLabel.setAfter(referenceStr + mentionOrReference);
                            referenceNumber = referenceStr;
                            isContinuosCoref = false;
                        } else if (referenceStr.matches("\\d+\\(\\*")) {
                            isContinuosCoref = true;
                            coreLabel.setAfter(referenceStr.replace("(*", "") + mentionOrReference);
                            referenceNumber = referenceStr.replace("(*", "");
                        } else if (referenceStr.matches("\\*") && isContinuosCoref) {
                            coreLabel.setAfter(referenceNumber + mentionOrReference);
                            isContinuosCoref = true;
                        } else if (referenceStr.matches("\\*\\)")) {
                            coreLabel.setAfter(referenceNumber + mentionOrReference);
                            isContinuosCoref = false;
                        }
                    }
                    sentenceCoreLabels.add(coreLabel);

                } else //end of sentence
                {
                    CoreMap coreMap = new ArrayCoreMap();
                    coreMap.set(CoreAnnotations.TokensAnnotation.class, sentenceCoreLabels);
                    coreMaps.add(coreMap);
                    allCoreLabels.addAll(sentenceCoreLabels);
                    sentenceCoreLabels = new ArrayList<CoreLabel>();

                }

            }
            annotation.set(CoreAnnotations.TokensAnnotation.class, allCoreLabels);
            annotation.set(CoreAnnotations.SentencesAnnotation.class, coreMaps);
            List<CorefChain> mainCorefChains = getCorefChainsFromGoldData(annotation);
            List<CorefChain> testCorefChains = new ReferenceFinder().extractChainsFromRawText(annotation);
            double accuracy = compareGoldAndTest(mainCorefChains, testCorefChains);
            System.out.println("Accuracy: "+accuracy/testCorefChains.size());
            System.out.println("Precision: "+accuracy/mainCorefChains.size());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static double compareGoldAndTest(List<CorefChain> mainCorefChains, List<CorefChain> testCorefChains) {
        int acc = 0;
        for (CorefChain corefChain : mainCorefChains) {
            if (isInTestCorefChains(corefChain, testCorefChains)) {
                acc++;
            }
        }
        return (double)acc;
    }

    private static boolean isInTestCorefChains(CorefChain corefChain, List<CorefChain> testCorefChains) {
        for (CorefChain corefChain1 : testCorefChains) {
            if (corefChain1.toString().equals(corefChain.toString()))
                return true;
        }
        return false;
    }

    private static List<CorefChain> getCorefChainsFromGoldData(Annotation annotation) {
        List<CorefChain> corefChains = new ArrayList<CorefChain>();
        List<CoreLabel> coreLabels = annotation.get(CoreAnnotations.TokensAnnotation.class);
        List<Mention> mentions = new ArrayList<Mention>();
        for (CoreLabel coreLabel : coreLabels) {
            if (coreLabel.word().matches("وی|او")) {
                mentions = new ArrayList<Mention>();
                Mention mention = new Mention();
                mention.setMentionCoreLabel(coreLabel);
                String refId = coreLabel.after().replace("_Men", "_Ref");
                List<CoreLabel> referenceCoreLabels = findReference(coreLabels, refId);
                //String referenceStr = getStringFromCoreLabels(referenceCoreLabels);
                ReferenceEntity referenceEntity = new ReferenceEntity();
                referenceEntity.setEntityTokens(referenceCoreLabels);
                CorefChain newChain = new CorefChain();
                newChain.setReferenceEntity(referenceEntity);

                mentions.add(mention);
                newChain.setMentions(mentions);
                if (newChain.getReferenceEntity().getEntityTokens().size() > 0)
                    corefChains.add(newChain);

            }

        }
        return corefChains;
    }

    private static String getStringFromCoreLabels(List<CoreLabel> referenceCoreLabels) {
        String str = "";
        for (CoreLabel coreLabel : referenceCoreLabels) {
            str += coreLabel.word() + " ";

        }
        return str.substring(0, str.length() - 1);
    }

    private static List<CoreLabel> findReference(List<CoreLabel> coreLabels, String refId) {
        List<CoreLabel> coreLabels1 = new ArrayList<CoreLabel>();
        for (CoreLabel coreLabel : coreLabels) {
            if (coreLabel.after().equals(refId)) {
                coreLabels1.add(coreLabel);
            }
        }
        return coreLabels1;
    }


    public static List<CoreLabel> readTokens(String filePath) {
        List<CoreLabel> coreLabels = new ArrayList<CoreLabel>();
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {

                String[] strs = strLine.split(" ");
                String[] corefTags = strs[3].split(",");
                String corefTag = "O";
                String nerTag = "O";
                if (strs[3].matches("SET.*_"))
                    nerTag = corefTags[corefTags.length - 1];
                else if (strs[3].matches("SET[^_]*"))
                    corefTag = corefTags[corefTags.length - 1];


                CoreLabel coreLabel = new CoreLabel();
                coreLabel.setWord(strs[3]);
                coreLabel.setTag(strs[2]);
                coreLabel.setNER(nerTag);
                coreLabel.set(CoreAnnotations.AfterAnnotation.class, corefTag);
                coreLabels.add(coreLabel);

            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return coreLabels;
    }

    public void testCorefSpeed() {

    }
}
