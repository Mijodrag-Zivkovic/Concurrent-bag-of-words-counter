package RAF.KiDSDomaci1.workers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CruncherWordCounterWorker implements Callable<Integer> {

    private String text;
    private ConcurrentHashMap<String,Long> wordOcurrences;
    private int limit;
    private int begin;
    private int arrity;
    private int textLength;
    private int originalBegin;

    public CruncherWordCounterWorker(String text, ConcurrentHashMap<String, Long> wordOcurrences,
                                     int limit, int index, int arrity) {
        this.text = text;
        this.wordOcurrences = wordOcurrences;
        this.limit = limit;
        this.begin = index;
        this.arrity = arrity;
        this.textLength = text.length();
    }

    @Override
    public Integer call() throws Exception {
        this.originalBegin = begin;
        int end = begin + limit - 1 > textLength ? textLength - 1 : begin + limit - 1;
        for (int i = 0; i < arrity; i++)
        {
            if (end < textLength - 1)
            {
                boolean charAfterIsWhitespace = Character.isWhitespace(text.charAt(end + 1));
                boolean currentCharIsWhitespace = Character.isWhitespace(text.charAt(end));
                while (currentCharIsWhitespace)
                {
                    if (end <= begin)
                        break;
                    else {
                        end--;
                        currentCharIsWhitespace = Character.isWhitespace(text.charAt(end));
                    }
                }
                charAfterIsWhitespace = Character.isWhitespace(text.charAt(end + 1));
                currentCharIsWhitespace = Character.isWhitespace(text.charAt(end));
                //while (!(!currentCharIsWhitespace && charAfterIsWhitespace))
                while(currentCharIsWhitespace || !charAfterIsWhitespace)
                {

                    end++;
                    if (end >= textLength - 1)
                        break;
                    charAfterIsWhitespace = Character.isWhitespace(text.charAt(end + 1));
                    currentCharIsWhitespace = Character.isWhitespace(text.charAt(end));

                }
                if (i < arrity-1 && end<textLength-1) {
                    end++;
                    while(Character.isWhitespace(text.charAt(end)))
                    {
                        end++;
                    }
                }

            }
        }
        if (begin > 0) {

            boolean charBeforeIsWhitespace = Character.isWhitespace(text.charAt(begin - 1));
            boolean currentCharIsWhitespace = Character.isWhitespace(text.charAt(begin));
            while (!(!currentCharIsWhitespace && charBeforeIsWhitespace)) {
                if (begin >= end)
                    break;
                else {
                    begin++;
                    charBeforeIsWhitespace = Character.isWhitespace(text.charAt(begin - 1));
                    currentCharIsWhitespace = Character.isWhitespace(text.charAt(begin));
                }
            }
        }

        //System.out.println(originalBegin + " Begin " + begin + " End " + end);
        if (begin < end)
        {
            try
            {
                List<String> words = textToWords(begin, end);
                //System.out.println(originalBegin + " " + words);
                for (int i=0;i< words.size();i++)
                {
                    List<String> sortedWords = new ArrayList<>();
                    for (int j=i;j<i+arrity;j++)
                    {
                        sortedWords.add(words.get(j));

                    }
                    //Collections.sort(sortedWords);
                    sortedWords = sortedWords.stream().sorted().collect(Collectors.toList());
                    String finalString = listToString(sortedWords);
                    Long occurence = wordOcurrences.putIfAbsent(finalString,1L);
                    //System.out.println("occurence: " + occurence);
                    if (occurence != null)
                    {
                        wordOcurrences.compute(finalString,(key, value) -> value+1);
                    }
                }
            }catch (OutOfMemoryError e){
                System.out.println("out of mem crunch");
            }



        }

        return 1;

    }
    private List<String> textToWords(int startIndex, int endIndex) {
        List<String> words = new ArrayList<>();
        int begin = startIndex;
        for (int i = startIndex; i < endIndex; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                continue;
            }
            words.add(this.text.substring(begin, i).intern());
            i++;
            while(Character.isWhitespace(text.charAt(i)))
                i++;
            begin = i;
        }
        words.add(this.text.substring(begin, endIndex+1).intern());
        return words;
    }

    private String listToString(List<String> words) {
        if (arrity == 1) {
            return words.get(0);
        }
        StringBuilder result = new StringBuilder(words.get(0));
        for (int i = 1; i < arrity; i++) {
            result.append(" " + words.get(i));
        }
        return result.toString();
    }
//    @Override
//    public Integer call() throws Exception {
//        for (int i = index; i < index+limit; i++)
//        {
//            //if da li smo prekoracili length
//            if (i+arrity-1<textLength)
//            {
//                List<String> words = new ArrayList<>();
//                for (int j = i; j<i+arrity; j++)
//                {
//
//                    words.add(splitText[j]);
//                }
//                words = words.stream().sorted().collect(Collectors.toList());
//                //System.out.println(words);
//                String finalWords="";
//                for (String word : words)
//                {
//                    finalWords+=word;
//                    //finalWords+=" ";
//                }
//                Long occurence = wordOcurrences.putIfAbsent(finalWords,1L);
//                //System.out.println("occurence: " + occurence);
//                if (occurence != null)
//                {
//                    wordOcurrences.compute(finalWords,(key, value) -> value+1);
//                }
//            }
//        }
//        return 1;
//    }


}
