package br.ufrn.imd.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import br.ufrn.imd.core.Dataset;
import br.ufrn.imd.core.ExecutionResult;
import br.ufrn.imd.core.IterationResult;
import br.ufrn.imd.core.Measures;
import br.ufrn.imd.filemanipulation.FileOutputWriter;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class Main10FoldNatassia {

	public static ArrayList<Integer> seeds;
	public static ArrayList<ArrayList<String>> groups;
	public static ArrayList<Dataset> datasets;
	public static ArrayList<Classifier> classifiers;
	public static int numIterations = 1;
	public static int numFolds = 10;

	public static Dataset trainDataset;
	public static Dataset testDataset;

	public static String inputPath = "src/main/resources/datasets/";
	public static String outputPath = "src/main/resources/results/";
	public static FileOutputWriter writer;
	public static FileOutputWriter summarizer;

	public static ExecutionResult execRes;
	
	public static void main(String[] args) throws Exception {
		
		seeds = new ArrayList<Integer>();
		groups = new ArrayList<ArrayList<String>>();
		datasets = new ArrayList<Dataset>();
		classifiers = new ArrayList<Classifier>();
		
		Measures measures;

		configureClassifiers();
		populateSeeds();
		populateDatasets();

		numIterations = seeds.size();
		IterationResult iterRes;

		summarizer = new FileOutputWriter(outputPath + "_geral");
		
		StringBuilder sb = new StringBuilder();
		
		ArrayList<String> accuracies = new ArrayList<String>();
		ArrayList<String> fMeasures = new ArrayList<String>();
		
		Dataset trainTemp = new Dataset();
		Dataset testTemp = new Dataset();
		
		for (ArrayList<String> group : groups) {
			for(Classifier classifier: classifiers) {					
					
				for (int i = 0; i < group.size(); i++) {
					for (int j = 0; j < group.size(); j++) {
						
						accuracies = new ArrayList<String>();
						fMeasures = new ArrayList<String>();
						
						if(i != j) {
							sb = new StringBuilder();
							for(Integer seed: seeds) {
								
								trainTemp = new Dataset(buildDataset(group.get(i)));
								trainTemp.getInstances().randomize(new Random(seed));
								testTemp = new Dataset(buildDataset(group.get(j)));
								testTemp.getInstances().randomize(new Random(seed));
								
								execRes = new ExecutionResult(numFolds, trainTemp.getDatasetName(), testTemp.getDatasetName());
			
								for (int n = 0; n < numFolds; n++) {
									iterRes = new IterationResult();
			
									trainDataset = new Dataset(trainTemp.getInstances().trainCV(numFolds, n));
									testDataset = new Dataset(testTemp.getInstances().testCV(numFolds, n));
									
									//System.out.println(trainDataset.getInstances().get(0));
									
									iterRes.setTrainTestProportion(new String(numFolds + "-fold"));
			
									execRes.setBegin(System.currentTimeMillis());
									classifier.buildClassifier(trainDataset.getInstances());
									measures = new Measures(classifier, trainDataset.getInstances(), testDataset.getInstances());
									execRes.setEnd(System.currentTimeMillis());
			
									putResultsInIterationResult(measures, iterRes);
									execRes.addIterationResult(iterRes);
								}
								saveExecutionResultOnFile(writer);								
								accuracies.add(execRes.getAccuracyValuesAsString());
								fMeasures.add(execRes.getFmeasureValuesAsString());
							}
							sb.append(trainTemp.getDatasetName());
							sb.append(";");
							sb.append(testTemp.getDatasetName());
							sb.append(";");
							sb.append(classifier.getClass().getSimpleName());
							sb.append(";");
							sb.append(numFolds);
							sb.append(";");
							sb.append(accuracies.toString());
							sb.append(fMeasures.toString());
							
							System.out.println(sb.toString());
							summarizer.addContentline(sb.toString());
						}
					}
				}
			}
		}
		summarizer.writeInFile();
		summarizer.saveAndClose();
	}


	public static void populateDatasets() {

		ArrayList<String> groupAdm = new ArrayList<String>();
		groupAdm.add("exp_3_kmeans_5_7_g0.arff");
		groupAdm.add("exp_3_kmeans_5_7_g1.arff");
		groupAdm.add("exp_3_kmeans_5_7_g2.arff");
		groupAdm.add("exp_3_kmeans_5_7_g3.arff");
		groupAdm.add("exp_3_kmeans_5_7_g4.arff");

		groups.add(groupAdm);
	}

	public static void configureClassifiers() {
		IBk ibk = new IBk();
		J48 j48 = new J48();
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		NaiveBayes naive = new NaiveBayes();
		SMO smo = new SMO();
	
		RandomForest randomForest = new RandomForest();
		Bagging bagging = new Bagging();
		AdaBoostM1 adaBoostM1 = new AdaBoostM1();
		try {
			j48.setOptions(weka.core.Utils.splitOptions("-C 0.25 -M 2"));
			adaBoostM1.setOptions(weka.core.Utils.splitOptions("-P 100 -S 1 -I 10 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		classifiers.add(ibk);
		classifiers.add(j48);
		classifiers.add(mlp);
		classifiers.add(naive);
		classifiers.add(smo);
		
		classifiers.add(randomForest);
		classifiers.add(bagging);
		classifiers.add(adaBoostM1);
			
	}

	public static void populateSeeds() {
		seeds.add(1);
		seeds.add(7); 
		seeds.add(11); 
		seeds.add(17); 
		seeds.add(33); 
		seeds.add(57);
		seeds.add(67); 
		seeds.add(113); 
		seeds.add(117); 
		seeds.add(351);
	}

	public static Dataset buildDataset(String datasetName) {
		Dataset d = null;
		try {
			d = new Dataset(inputPath + datasetName);
		} catch (Exception e) {
			System.out.println("ERRO AO CRIAR O DATASET COM NOME: " + datasetName);
			e.printStackTrace();
		}
		return d;
	}

	public static void putResultsInIterationResult(Measures measures, IterationResult iterResult) {
		iterResult.setAccuracy(measures.getAccuracy());
		iterResult.setError(measures.getError());
		iterResult.setfMeasure(measures.getFmeasureMean());
		iterResult.setPrecision(measures.getPrecisionMean());
		iterResult.setRecall(measures.getRecallMean());
		iterResult.setRoc(measures.getRoc());
	}

	public static String buildFileName(ExecutionResult execRes) {
		StringBuilder sb = new StringBuilder();
		sb.append(outputPath);
		sb.append(execRes.getTrainDataset());
		sb.append("_");
		sb.append(execRes.getTestDataset());
		sb.append("_");
		sb.append(execRes.getResults().get(0).getTrainTestProportion());
		return sb.toString();
	}

	public static void saveExecutionResultOnFile(FileOutputWriter writer) throws IOException {
		writer = new FileOutputWriter(buildFileName(execRes));
		writer.addContentline(execRes.getFinalResult());
		writer.writeInFile();
		writer.saveAndClose();
	}

	public static String buildResultLineForSummary(ExecutionResult exec) {
		StringBuilder sb = new StringBuilder();
		sb.append(execRes.getTrainDataset());
		sb.append("_");
		sb.append(execRes.getTestDataset());
		sb.append("_");
		sb.append(execRes.getResults().get(0).getTrainTestProportion());
		sb.append("\t");
		sb.append(exec.getPrecisionValues());

		return sb.toString();
	}
	
	/**
	 * 
	 * Coleta o nome da base de teste, o nome da base de treino, o método
	 * e os resultados de todos os folds das métricas escolhidas (acc e F-measure)
	 * 
	 * @param execRes
	 * @return
	 */
	public static ArrayList<String> getResultsExec(ExecutionResult execRes, Classifier classifier){
		ArrayList<String> results = new ArrayList<String>();
		results.add(execRes.getTrainDataset());
		results.add(execRes.getTestDataset());
		//results.add(classifier.)
		results.add(execRes.getAccuracyValuesAsString());
		results.add(execRes.getFmeasureValuesAsString());
		return results;
	}
}
