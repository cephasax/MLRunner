package br.ufrn.imd.main;

import java.util.ArrayList;

import br.ufrn.imd.core.Dataset;
import br.ufrn.imd.core.ExecutionResult;

public class Main {

	public static ArrayList<Integer> seeds;
	public static ArrayList<ArrayList<String>> groups;
	public static ArrayList<Dataset> datasets;
	public static ArrayList<String> trainTestvalues;

	public static int numIterations = 10;

	public static Dataset trainDataset;
	public static Dataset testDataset;

	public static String inputPath = "src/main/resources/datasets/";
	public static String outputPath = "src/main/resources/results/";
	
	public static ExecutionResult execRes;

	public static void main(String[] args) throws Exception {

		seeds = new ArrayList<Integer>();
		groups = new ArrayList<ArrayList<String>>();
		datasets = new ArrayList<Dataset>();
		trainTestvalues = new ArrayList<String>();

		populateSeeds();
		populateDatasets();

		int trainPercent = 0;
		int testPercent = 0;

		for (ArrayList<String> group : groups) {
			for (String splitWay : trainTestvalues) {
				trainPercent = Integer.valueOf(splitWay.substring(0, 2));
				testPercent = Integer.valueOf(splitWay.substring(3, 5));

				for (int i = 0; i < numIterations; i++) {
					Dataset trainTemp = new Dataset(buildDataset(group.get(i)));
					for (int j = 0; j < numIterations; j++) {
						Dataset testTemp = new Dataset(buildDataset(group.get(j)));
						
						//parametros precisam ter seed, splitway... to add
						execRes = new ExecutionResult(numIterations, trainTemp.getDatasetName(), testTemp.getDatasetName());
						
						for (Integer seed : seeds) {
							trainDataset = Dataset.getSubsetFromDataset(trainTemp, trainPercent, seed);
							testDataset = Dataset.getSubsetFromDataset(testTemp, testPercent, seed);
							
							
							//criar executor
							//executar método
							//capturar resultados da iteração
							//adicionar resultados da iteração ao geral
							//gerar arquivo de saida
							//guardar resumo para .txt geral
							//printar resumo
							
						}
					}
				}

			}
		}
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
		trainTestvalues.add("70-30");
		trainTestvalues.add("50-50");
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

	public static void buildExecutionResult() {
		
		
		
	}
}
