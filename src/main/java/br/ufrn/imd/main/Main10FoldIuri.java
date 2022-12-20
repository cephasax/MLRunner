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
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class Main10FoldIuri {

	public static ArrayList<Integer> seeds;
	public static ArrayList<ArrayList<String>> groups;
	public static ArrayList<ArrayList<String>> groupTest;
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
		groupTest = new ArrayList<ArrayList<String>>();
		datasets = new ArrayList<Dataset>();
		classifiers = new ArrayList<Classifier>();
		
		Measures measures;

		configureClassifiers();
		populateSeeds();
		populateDatasets();
		populateDatasetTest();

		numIterations = seeds.size();
		IterationResult iterRes;

		summarizer = new FileOutputWriter(outputPath + "_geral");
		
		StringBuilder sb = new StringBuilder();
		
		ArrayList<String> accuracies = new ArrayList<String>();
		ArrayList<String> fMeasures = new ArrayList<String>();
		
		Dataset trainTemp = new Dataset();
		Dataset testTemp = new Dataset();
		
		//grupos - só há um grupo, com os datasets de treino.
		for (ArrayList<String> group : groups) {
			//cada classificador - são 8 classificadores
			for(Classifier classifier: classifiers) {					
				
				//cada dataset de treino do grupo - são 6 datasets
				for (int i = 0; i < group.size(); i++) {
						
					accuracies = new ArrayList<String>();
					fMeasures = new ArrayList<String>();
					sb = new StringBuilder();
					
					//para cada seed - são 10 (logo serão feitas 10 execuções)
					for(Integer seed: seeds) {
						
						//pega a base de treino i
						trainTemp = new Dataset(buildDataset(group.get(i)));
						trainTemp.getInstances().randomize(new Random(seed));
						
						//pega a única base de testes
						testTemp = new Dataset(buildDataset(groupTest.get(0).get(0)));//base unica
						testTemp.getInstances().randomize(new Random(seed));
						
						//cria um coletor de resultados com numFolds slots para as numFolds iterações (folds)
						execRes = new ExecutionResult(numFolds, trainTemp.getDatasetName(), testTemp.getDatasetName());
						execRes.setBegin(System.currentTimeMillis());
						//para cada fold
						for (int n = 0; n < numFolds; n++) {
							
							//cria um coletor de resultado da iteração
							iterRes = new IterationResult();
							
							//pega numfolds -1 folds do dataset de treino (funciona internamente no weka)
							trainDataset = new Dataset(trainTemp.getInstances().trainCV(numFolds, n));
							//pega 1 fold do dataset de teste (funciona internamente no weka)
							testDataset = new Dataset(testTemp.getInstances().testCV(numFolds, n));						
							
							//treina o classificador com a base de treino, executa o sobre a base de teste. 
							classifier.buildClassifier(trainDataset.getInstances());
							measures = new Measures(classifier, trainDataset.getInstances(), testDataset.getInstances());
							
							//coloca os resultados da iteração dentro do coletor da iteração e depois coloca a iteração no coletor de resultados geral
							putResultsInIterationResult(measures, iterRes);
							execRes.addIterationResult(iterRes);
						}
						execRes.setEnd(System.currentTimeMillis());
						
						//salva o resultado da execução num arquivo à parte - comentar para deixar apenas arquivo geral;
						saveExecutionResultOnFile(writer);								
						
						//captura as acurácias e as fmeasures obtidas. serão arrays com tamanho = numFolds
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
					sb.append("; accuracies: "); 
					sb.append(accuracies.toString());
					sb.append("; fmeasures: "); 
					sb.append(fMeasures.toString());
					  
					System.out.println(sb.toString()); 
					summarizer.addContentline(sb.toString());
					 
				}
			}
		}
		summarizer.writeInFile();
		summarizer.saveAndClose();
	}


	public static void populateDatasets() {

		ArrayList<String> group01 = new ArrayList<String>();
		
		group01.add("500_inst_uniq_14_classes.arff");
		group01.add("1000_inst_uniq_14_classes.arff");
		group01.add("2000_inst_uniq_14_classes.arff");
		group01.add("3000_inst_uniq_14_classes.arff");
		group01.add("4000_inst_uniq_14_classes.arff");
		group01.add("5000_inst_uniq_14_classes.arff");
		
		groups.add(group01);
	}
	
	public static void populateDatasetTest() {
		ArrayList<String> group01_test = new ArrayList<String>();
		group01_test.add("dataset_001_inst_uniq_065_14_classes_random.arff");
		groupTest.add(group01_test);
	}

	public static void configureClassifiers() {
		IBk ibk = new IBk();
		J48 j48 = new J48();
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		NaiveBayes naive = new NaiveBayes();
		SMO smo = new SMO();
	
		RandomForest randomForest = new RandomForest();
		Bagging bagging = new Bagging();
		RandomTree randomTree = new RandomTree();
//		DecisionTree decisionTree = new DecisionTree();
		DecisionTable decisionTable = new DecisionTable();
		
//		AdaBoostM1 adaBoostM1 = new AdaBoostM1();
//		try {
//			j48.setOptions(weka.core.Utils.splitOptions("-C 0.25 -M 2"));
//			adaBoostM1.setOptions(weka.core.Utils.splitOptions("-P 100 -S 1 -I 10 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		classifiers.add(ibk);
		classifiers.add(j48);
		classifiers.add(mlp);
		classifiers.add(naive);
		classifiers.add(smo);
//		
		classifiers.add(randomForest);
		classifiers.add(bagging);
//		classifiers.add(adaBoostM1);
		classifiers.add(randomTree);
//		classifiers.add(decisionTree);
		classifiers.add(decisionTable);
		
			
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