package com.SpamDetection;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by elf on 10.05.2016.
 */
public class BiGram {

    public BiGram(double biGramMatris[][], ArrayList<double[][]> biGramMatrisList){

        //System.out.print("SİZE: " + biGramMatris.length + "\n");
        for(int i=0; i<biGramMatrisList.size(); ++i) {
            for (int m = 0; m < biGramMatris.length; ++m) {
                for (int n = 0; n < biGramMatris.length; ++n) {
                    //System.out.println(m + "  " + n);
                    if( biGramMatris[m][n] != 0 && biGramMatrisList.get(i)[m][n] != 0)
                        //biGramMatrisList.get(i)[m][n] /= biGramMatris[m][n];

                    biGramMatrisList.get(i)[m][n] = Math.round(( biGramMatrisList.get(i)[m][n]/ (biGramMatris[m][n])) * 1000.0) / 1000.0 ;

                }
            }
        }
    }

}
