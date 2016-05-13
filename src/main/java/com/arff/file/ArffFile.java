package com.arff.file;

/**
 * Created by elf on 03.05.2016.
 */


import com.SpamDetection.TweetInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ArffFile implements Serializable {
    private String relation = "twitter_spam_data";
    ArrayList<String> TweetValues;

    private final String[] control = {  "LengthOfProfileName" , "LengthOfProfileDescription", "NumberOfFollowings", "NumberOfFollowers",
                                        "NumberOfTweetsPost" , "AgeOfTheUserAccount", "RatioOfNumberOfFollowingsAndFollowers", "ReputationOfTheUser",
                                        "FollowingRate" , "NumberOfWords", "NumberOfCharacters",
                                        "NumberOfCapitalizationCharacter" , "NumberOfCapitalizationWordPerWord", "MaxWordLength", "MeanWordLength",
                                        "NumberOfExclamationMark" , "NumberOfQuestionMark", "NumberOfURL", "URLPerWord",
                                        "NumberOfHashtag" , "HashtagPerWord", "NumberOfMention", "MentionPerWord",
                                        "NumberOfSpamWord", "SpamWordPerWord"
                                       };

    public String toString(ArrayList<double[][]> biGramMatrisList) {
        /*
        @relation 'twitter_spam_detection'
        @attribute word1 {0,1}
        ...
        @attribute wordn {0,1}

        @data
        0,0,0,0,1,0,1...class
        */

        StringBuilder content = new StringBuilder();
        content.append("@relation '").append(relation).append("'\n");


        for(int i=0; i<control.length; ++i){
            content.append("@attribute ").append(control[i]).append(" NUMERIC").append("\n");
        }

        String biGramList = "abcdefghijklmnopqrstuvwxyz";

        for(int i=0; i<biGramList.length(); ++i){
            for(int j=0; j<biGramList.length(); ++j){
                content.append("@attribute ").append(biGramList.charAt(i)).append(biGramList.charAt(j)).append(" NUMERIC").append("\n");
            }
        }


        //content.append("@ATTRIBUTE class {spam,legitimate}\n");
        content.append("@ATTRIBUTE class {0,1}\n");

        content.append("@data \n");

//System.out.print("HÖBÖLÜ::: " + biGramMatrisList.size() + "  " + TweetValues.size());

        for(int i=0; i<biGramMatrisList.size(); ++i) {
            String biGramValues = "";
            for (int m = 0; m < 26; ++m) {
                for (int n = 0; n < 26; ++n) {

                    biGramValues = biGramValues + "," + biGramMatrisList.get(i)[m][n];
                    //System.out.println(biGramValues);
                }
            }

            content.append(TweetValues.get(i)).append(biGramValues).append("\n");
        }

/*
        for(int i=0; i<TweetValues.size(); ++i){
        }
*/
        return content.toString();
    }


    public boolean saveArffFile(String savePath, ArrayList<double[][]> biGramMatrisList){
        try {
            Writer output;
            File file = new File(savePath);
            output = new BufferedWriter(new FileWriter(file));
            output.write(this.toString(biGramMatrisList));
            output.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public ArffFile(ArrayList<String> TweetValues){
        this.TweetValues = TweetValues;
    }

}