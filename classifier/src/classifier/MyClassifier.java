package classifier;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.List;


import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.AttributeSelection;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;

import weka.core.Instances;
import weka.core.Instance;
import weka.core.DenseInstance;

public class MyClassifier {
    private Instances data;
    private Instances test;
    private Classifier classifier;
    private double time; //tempo que ultimo treinamento durou em ns

    public static void print(Object a) {
        System.out.println(a);
    }

    public static void print(Object[] a) {
        print(Arrays.toString(a));
    }

    public MyClassifier(String arff, int nfeatures, int percentageTraining, String classifier)
            throws Exception {
        DataSource source = new DataSource(arff);

        data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        if (nfeatures > 0){
            featureSelectionIG (nfeatures, arff + ".infogain"); //fazendo feature selection
            print("Com information gain de " + nfeatures + " features.");
        } else {
            print("Sem information gain.");
        }
        splitTest(percentageTraining); //separando um pouco para treino
        classifierTypeChoice(classifier);
        print(classifier + " treinamento em " + getTime() + " ms");
        print(testing());
    }

    public double getTime() {
        return this.time / 1000000;
    }

    public double[] classify(List<String> page) throws Exception {
        /** Recebe uma lista com palavras filtradas de uma pagina e retorna um array de double com a probabilidade dela assumir valores de sua classIndex */
        Hashtable wordsmap = new Hashtable<String, Integer>();
        for (int i = 0; i < page.size() - 1; i++) { //conta qtas vezes cada palavra da page ocorre
            String word = page.get(i);
            int qtde = 0; //qtde de vezes da palavra da pagina nela propria
            if (wordsmap.containsKey(word)) {
                qtde = (int) wordsmap.get(word);
            }
            qtde++;
            wordsmap.put(word, qtde);
        }
        int[] values = new int[data.numAttributes() - 1]; //vai guardar os valores da pagina para cada atributo
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            String word = data.attribute(i).name();
            int qtde = 0; //qtde de vezes de uma palavra dos atributos na pagina
            if (wordsmap.containsKey(word)) {
                qtde = (int) wordsmap.get(word);
            } else{
            }
            values[i] = qtde; //guardando os valores do site para cada atributo do arff
        }
        Instance site = new DenseInstance(data.numAttributes());
        site.setDataset(data); //classifier nao tem acesso ao dataset, ai tem que coloca-lo na instance a ser classificada
        for (int i=0; i< data.numAttributes() - 1; i++){
            site.setValue(i, values[i]);
        }

        // print(classifier.distributionForInstance(site)[0]);
        return classifier.distributionForInstance(site);
    }

    public void featureSelectionIG(int n, String arffn) throws Exception {
        AttributeSelection selector = new AttributeSelection();
        InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
        Ranker ranker = new Ranker();
        ranker.setNumToSelect(Math.min(n, data.numAttributes() - 1));
        selector.setEvaluator(evaluator);
        selector.setSearch(ranker);
        selector.SelectAttributes(data);
        this.data = selector.reduceDimensionality(data);
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(arffn));
        saver.writeBatch();
    }

    public void splitTest(int porcent) throws Exception {
        /** Separa porcent% Instances para treinamento e o resto para testes */
        if (porcent > 100 || porcent < 0) {
            throw new Exception("Porcentagem em split invalida, tente um número entre 0 e 100");
        }
        data.randomize(new Random(4)); //baguncando ordem, pois a do arff esta por site (e assim ele nao aprenderia sobre outros sites)
        int trainSize = (int) Math.round(data.numInstances() * porcent / 100);
        int testSize = data.numInstances() - trainSize;
        test = new Instances(data, trainSize, testSize);
        data = new Instances(data, 0, trainSize);
    }

    public void classifierTypeChoice(String type){
        Classifier c = null;
        if (type.equals("bayes"))
            c = new NaiveBayes();
        else if (type.equals("j48"))
            c = new J48();
        else if (type.equals("smo"))
            c = new SMO();
        else if (type.equals("logistic"))
            c = new Logistic();
        else if (type.equals("mlp"))
            c = new MultilayerPerceptron();
        classifier = c;
        double t = System.nanoTime();
        try {
            classifier.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        time = System.nanoTime() - t;
    }


    public String testing() throws Exception {
        Evaluation e = new Evaluation(data);
        e.evaluateModel(classifier, test);
        return e.toSummaryString("\nResultados\n--------------\n", false) + "\n" + e.toClassDetailsString() + "\n" + e.toMatrixString();
    }

    public Instances getData() {
        return this.data;
    }

    public Instances getTest() {
        return this.test;
    }
}
