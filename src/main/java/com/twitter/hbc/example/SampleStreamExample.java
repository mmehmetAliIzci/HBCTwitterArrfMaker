/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.twitter.hbc.example;

import com.SpamDetection.BiGram;
import com.SpamDetection.TweetInfo;
import com.SpamDetection.isSpam;
import com.arff.file.ArffFile;
import com.com.Json.Parser.ConvertJSON;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class SampleStreamExample {


    private static String biGramList = "abcdefghijklmnopqrstuvwxyz";
    private static double biGramMatris[][] = new double[biGramList.length()][biGramList.length()];
    private static ArrayList<double[][]> biGramMatrisList = new ArrayList<double[][]>();

    private static ArrayList<TweetInfo> tweetList = new ArrayList<TweetInfo>();

    private static final Pattern SPACE = Pattern.compile(" ");
    public static int tweetCount = 100;


    public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
        // Create an appropriately sized blocking queue


        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        // add some track terms
        endpoint.trackTerms(Lists.newArrayList("Darwyn Cooke", "#Fridaythe13th","Faking It"));

        endpoint.stallWarnings(false);

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        //Authentication auth = new com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

        // Create a new BasicClient. By default gzip is enabled.
        BasicClient client = new ClientBuilder()
                .name("sampleExampleClient")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Establish a connection
        client.connect();
        ArrayList<String> TweetValues = new ArrayList<>();


        String firstch, secondch;
        for(int i=0; i<biGramList.length()-1; ++i)
            for(int j=0; j<biGramList.length()-1; ++j)
                biGramMatris[i][j] = 0.0;



        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < tweetCount; msgRead++) {
            if (client.isDone()) {

              System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
              break;
            }

            String msg = queue.poll(5, TimeUnit.SECONDS);

            if (msg == null) {
              System.out.println("Did not receive a message in 5 seconds");
            }else {

                if(!msg.contains("\"delete\""))   // gelen delete'li tweetleri eliyor
                {
                    //System.out.print(msg);
                    JSONObject jsonObject = ConvertJSON.stringToJson(msg);
                    try {
                      if((jsonObject.get("lang").toString().equals("en")) && !(StringUtils.substring(jsonObject.get("text").toString(), 0, 2).equalsIgnoreCase("RT"))){
                          System.out.println(StringUtils.substring(jsonObject.get("text").toString(), 0, 2));
                          isSpam isSpam = new isSpam(jsonObject, TweetValues, biGramList, biGramMatris, biGramMatrisList);
                      }
                    } catch (JSONException e) {
                      e.printStackTrace();
                    }
                }
            }
        }

        new BiGram(biGramMatris, biGramMatrisList);
        ArffFile arff = new ArffFile(TweetValues);
        if(arff.saveArffFile("spam.arff", biGramMatrisList))
            System.out.println("SPAM.ARFF Succeed");



        client.stop();

        // Print some stats
        System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
      }


      public static void main(String[] args) {
        try {
          SampleStreamExample.run("naAb9r49Na4fRwPaTZnQthypk", "reSg5nAKr2rU1TxeCff3uDrNL1TbizpDsVe0rS8h02mGK5ybmS", "706562595134828548-v46p6ROA9P5OKp3p3UKpnXbLovtru5m", "S3RWCFcx0oGBwmOa4LhHrf5cC5FkEKFXlcObqlIdaoAjh");
        } catch (InterruptedException e) {
          System.out.println(e);
        }
      }
 }
