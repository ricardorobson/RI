package classifier;

import java.io.IOException;
import java.util.ArrayList;

public class Main{

    public static String arffName = "psicofeatures.arff";
    public static final String NB = "bayes";
    public static final String J48 = "j48";
    public static final String SMO = "smo";
    public static final String LOGISTIC = "logistic";
    public static final String MLP = "perceptron";

    public static void main(String[] args){
        Preprocessing p;
        try {
            p = new Preprocessing("./training/falses", "./training/true", "./training/maybe", arffName, null );
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyClassifier classifier = null;
        try {
            classifier = new MyClassifier(arffName, 30, 75, LOGISTIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        classifyFiles(classifier);
    }

    public static void classifyFiles( MyClassifier classifier){
        ArrayList<String> outs = new ArrayList<String>();
        for (String file: Preprocessing.getFiles("./classification")){
            double[] prob = new double[0];
            try {
                prob = classifier.classify(Parse.parse(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (prob[1] > 0.5){
                outs.add('-' + file + " Classificado como: True");
            } else {
                outs.add('-' + file + " Classificado como: False");
            }

        }
        WriteOut.writeOut("outFiles.txt", outs);
    }
}
