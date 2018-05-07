package classifier;

import java.io.IOException;

public class Main{

    public static String arffName = "psicofeatures.arff";
    public static final String NB = "bayes";
    public static final String J48 = "j48";
    public static final String SMO = "smo";
    public static final String LOGISTIC = "logistic";
    public static final String MLP = "perceptron";


    public static void print(Object a) {
        System.out.println(a);
    }

    public static void main(String[] args){
        Preprocessing p;
        try {
            p = new Preprocessing("./training/false", "./training/true", arffName, null );
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyClassifier classifier = null;
        try {
            classifier = new MyClassifier(arffName, 30, 75, J48);
        } catch (Exception e) {
            e.printStackTrace();
        }

        classifyFiles(classifier);
    }

    public static void classifyFiles( MyClassifier classifier){
        for (String file: Preprocessing.getFiles("./classification")){
            double[] prob = new double[0];
            try {
                prob = classifier.classify(Parse.parse(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (prob[1] > 0.5){
                print('-' + file + " Classificado como: True");
            } else {
                print('-' + file + " Classificado como: False");
            }
        }
    }
}
