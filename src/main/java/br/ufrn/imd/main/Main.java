package br.ufrn.imd.main;

import java.util.ArrayList;

import br.ufrn.imd.core.Dataset;
import br.ufrn.imd.core.ExecutionResult;
import br.ufrn.imd.core.IterationResult;
import br.ufrn.imd.core.Measures;
import br.ufrn.imd.filemanipulation.FileOutputWriter;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

public class Main {

	public static ArrayList<Integer> seeds;
	public static ArrayList<ArrayList<String>> groups;
	public static ArrayList<Dataset> datasets;
	public static ArrayList<String> trainTestproportion;

	public static int numIterations = 10;

	public static Dataset trainDataset;
	public static Dataset testDataset;

	public static String inputPath = "src/main/resources/datasets/";
	public static String outputPath = "src/main/resources/results/";
	public static FileOutputWriter writer;

	public static ExecutionResult execRes;

	public static Classifier classifier;

	public static void main(String[] args) throws Exception {

		seeds = new ArrayList<Integer>();
		groups = new ArrayList<ArrayList<String>>();
		datasets = new ArrayList<Dataset>();
		trainTestproportion = new ArrayList<String>();
		Measures measures;

		configureClassifier();
		populateSeeds();
		populateDatasets();
		populateTrainTestValues();

		int trainPercent = 0;
		int testPercent = 0;

		for (ArrayList<String> group : groups) {
			for (String trainTestP : trainTestproportion) {
				trainPercent = Integer.valueOf(trainTestP.substring(0, 2));
				testPercent = Integer.valueOf(trainTestP.substring(3, 5));

				for (int i = 0; i < numIterations; i++) {
					Dataset trainTemp = new Dataset(buildDataset(group.get(i)));
					for (int j = 0; j < numIterations; j++) {
						Dataset testTemp = new Dataset(buildDataset(group.get(j)));

						execRes = new ExecutionResult(numIterations, trainTemp.getDatasetName(), testTemp.getDatasetName());
						IterationResult iterRes = new IterationResult();

						for (Integer seed : seeds) {
							trainDataset = Dataset.getSubsetFromDataset(trainTemp, trainPercent, seed);
							testDataset = Dataset.getSubsetFromDataset(testTemp, testPercent, seed);

							iterRes.setSeed(seed);
							iterRes.setTrainTestProportion(trainTestP);

							execRes.setBegin(System.currentTimeMillis());
							classifier.buildClassifier(trainDataset.getInstances());
							measures = new Measures(classifier, trainDataset.getInstances(), testDataset.getInstances());
							execRes.setEnd(System.currentTimeMillis());

							putResultsInIterationResult(measures, iterRes);
							execRes.addIterationResult(iterRes);
						}
						writer = new FileOutputWriter(buildFileName(execRes));
						writer.addContentline(execRes.getFinalResult());
						System.out.println(execRes.getFinalResult());
					}
				}

			}
		}
	}

	public static void configureClassifier() {
		J48 j48 = new J48();
		try {
			j48.setOptions(weka.core.Utils.splitOptions("-C 0.25 -M 2"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		classifier = (J48) j48;
	}

	public static void populateDatasets() {

		ArrayList<String> groupAdm = new ArrayList<String>();
		groupAdm.add("ADM1.csv.arff");

		groups.add(groupAdm);

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

	public static void populateTrainTestValues() {
		trainTestproportion.add("70-30");
		trainTestproportion.add("50-50");
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
		sb.append(execRes.getTrainDataset());
		sb.append("_");
		sb.append(execRes.getTestDataset());
		sb.append("_");
		sb.append(execRes.getResults().get(0));
		return sb.toString();
	}
}
