package br.ufrn.imd.main;

import java.io.IOException;
import java.util.ArrayList;

import br.ufrn.imd.core.Dataset;
import br.ufrn.imd.core.ExecutionResult;
import br.ufrn.imd.core.IterationResult;
import br.ufrn.imd.core.Measures;
import br.ufrn.imd.filemanipulation.FileOutputWriter;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

public class Main10Fold {

	public static ArrayList<Integer> seeds;
	public static ArrayList<ArrayList<String>> groups;
	public static ArrayList<Dataset> datasets;

	public static int numIterations = 1;
	public static int numFolds = 10;

	public static Dataset trainDataset;
	public static Dataset testDataset;

	public static String inputPath = "src/main/resources/datasets/";
	public static String outputPath = "src/main/resources/results/";
	public static FileOutputWriter writer;
	public static FileOutputWriter summarizer;

	public static ExecutionResult execRes;

	public static Classifier classifier;

	public static void main(String[] args) throws Exception {

		seeds = new ArrayList<Integer>();
		groups = new ArrayList<ArrayList<String>>();
		datasets = new ArrayList<Dataset>();
		Measures measures;

		configureClassifier();
		populateSeeds();
		populateDatasets();

		numIterations = seeds.size();
		IterationResult iterRes;

		summarizer = new FileOutputWriter(outputPath + "PRECISION_geral");

		for (ArrayList<String> group : groups) {

			for (int i = 0; i < group.size(); i++) {
				Dataset trainTemp = new Dataset(buildDataset(group.get(i)));
				for (int j = 0; j < group.size(); j++) {
					Dataset testTemp = new Dataset(buildDataset(group.get(j)));

					execRes = new ExecutionResult(numFolds, trainTemp.getDatasetName(), testTemp.getDatasetName());

					for (int n = 0; n < numFolds; n++) {
						iterRes = new IterationResult();

						trainDataset = new Dataset(trainTemp.getInstances().trainCV(numFolds, n));
						testDataset = new Dataset(testTemp.getInstances().testCV(numFolds, n));

						iterRes.setTrainTestProportion(new String(numFolds + "-fold"));

						execRes.setBegin(System.currentTimeMillis());
						classifier.buildClassifier(trainDataset.getInstances());
						measures = new Measures(classifier, trainDataset.getInstances(), testDataset.getInstances());
						execRes.setEnd(System.currentTimeMillis());

						putResultsInIterationResult(measures, iterRes);
						execRes.addIterationResult(iterRes);
					}
					saveExecutionResultOnFile(writer);
					summarizer.addContentline(buildResultLineForSummary(execRes));
				}
			}
			summarizer.addContentline("\n\n");
		}
		summarizer.writeInFile();
		summarizer.saveAndClose();
	}


	public static void populateDatasets() {

		ArrayList<String> groupAdm = new ArrayList<String>();
		groupAdm.add("ADM1.csv.arff");
		groupAdm.add("ADM2.csv.arff");
		groupAdm.add("ADMFIN1.csv.arff");
		groupAdm.add("AUDADM.csv.arff");
		groupAdm.add("DIRPER1.csv.arff");
		groupAdm.add("DIRPER2.csv.arff");
		groupAdm.add("MAREM2.csv.arff");
		groupAdm.add("PROINV1.csv.arff");
		groupAdm.add("PROINV2.csv.arff");
		groupAdm.add("TOMDES1.csv.arff");

		ArrayList<String> groupContab = new ArrayList<String>();
		groupContab.add("CONTA1.csv.arff");
		groupContab.add("CONTA2.csv.arff");
		groupContab.add("CONTA3.csv.arff");
		groupContab.add("CONTAGUB.csv.arff");
		groupContab.add("CONTASOC.csv.arff");
		groupContab.add("CONTASUP1.csv.arff");
		groupContab.add("CONTASUP2.csv.arff");
		groupContab.add("ECONO.csv.arff");
		groupContab.add("PERCONT.csv.arff");
		groupContab.add("PROYPRE.csv.arff");

		ArrayList<String> groupDir = new ArrayList<String>();
		groupDir.add("DERCON.csv.arff");
		groupDir.add("DERPEN.csv.arff");
		groupDir.add("DERPER.csv.arff");
		groupDir.add("DERPROCO.csv.arff");
		groupDir.add("DERTRI2.csv.arff");
		groupDir.add("ETIPRO.csv.arff");
		groupDir.add("PROEJE.csv.arff");
		groupDir.add("SOCJUR.csv.arff");

		ArrayList<String> groupEngSist = new ArrayList<String>();
		groupEngSist.add("FUNRED.csv.arff");
		groupEngSist.add("GESERP.csv.arff");
		groupEngSist.add("ININSI.csv.arff");
		groupEngSist.add("INSOFT1.csv.arff");
		groupEngSist.add("PROVIS2.csv.arff");
		groupEngSist.add("TECPRO.csv.arff");
		groupEngSist.add("TECSEG.csv.arff");
		
		groups.add(groupAdm);
		groups.add(groupContab);
		groups.add(groupDir);
		groups.add(groupEngSist);

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

	public static void populateDatasets2() {

		ArrayList<String> group0Ati = new ArrayList<String>();
		group0Ati.add("DERCON.csv.arff");
		group0Ati.add("DERPROCO.csv.arff");
		group0Ati.add("DERTRI2.csv.arff");
		group0Ati.add("FUNRED.csv.arff");
		group0Ati.add("GESERP.csv.arff");
		group0Ati.add("ININSI.csv.arff");
		group0Ati.add("INSOFT1.csv.arff");
		group0Ati.add("PROVIS2.csv.arff");
		group0Ati.add("TECPRO.csv.arff");
		group0Ati.add("TECSEG.csv.arff");

		ArrayList<String> group1Ati = new ArrayList<String>();
		group1Ati.add("ADM1.csv.arff");
		group1Ati.add("ADM2.csv.arff");
		group1Ati.add("ADMFIN1.csv.arff");
		group1Ati.add("AUDADM.csv.arff");
		group1Ati.add("CONTA1.csv.arff");
		group1Ati.add("CONTA2.csv.arff");
		group1Ati.add("CONTASOC.csv.arff");
		group1Ati.add("CONTASUP2.csv.arff");
		group1Ati.add("DERPEN.csv.arff");
		group1Ati.add("DERPER.csv.arff");
		group1Ati.add("ETIPRO.csv.arff");
		group1Ati.add("PERCONT.csv.arff");
		group1Ati.add("PROEJE.csv.arff");
		group1Ati.add("PROINV1.csv.arff");
		group1Ati.add("PROINV2.csv.arff");
		group1Ati.add("PROYPRE.csv.arff");
		group1Ati.add("SOCJUR.csv.arff");

		ArrayList<String> group2Ati = new ArrayList<String>();
		group2Ati.add("CONTA3.csv.arff");
		group2Ati.add("CONTAGUB.csv.arff");
		group2Ati.add("CONTASUP1.csv.arff");
		group2Ati.add("DIRPER1.csv.arff");
		group2Ati.add("DIRPER2.csv.arff");
		group2Ati.add("ECONO.csv.arff");
		group2Ati.add("MAREM2.csv.arff");
		group2Ati.add("TOMDES1.csv.arff");

		groups.add(group0Ati);
		groups.add(group1Ati);
		groups.add(group2Ati);

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
}
