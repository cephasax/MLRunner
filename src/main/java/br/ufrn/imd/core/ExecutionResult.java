package br.ufrn.imd.core;

import java.util.ArrayList;

import br.ufrn.imd.utils.DateUtils;


public class ExecutionResult {

	private int numIterations;
	private String trainDataset;
	private String testDataset;
	private ArrayList<IterationResult> results;
	private IterationResult averageResult;
	private long begin;
	private long end;

	public ExecutionResult(int numIterations, String trainDataset, String testDataset) {
		this.numIterations = numIterations;
		this.trainDataset = new String(trainDataset);
		this.testDataset = new String(testDataset);
		this.results = new ArrayList<IterationResult>();
		this.averageResult = new IterationResult();
	}
	
	public void addIterationResult(IterationResult result) {
		
		if(results.size() < numIterations) {
			this.results.add(result);
			if(results.size() == numIterations) {
				calcAverageResult();
			}
		}
		else {
			System.out.println("result from this execution is already full");
		}
	}
	
	public void calcAverageResult() {
		
		int num = results.size();
		
		double accuracy = 0.0;
		double error = 0.0;
		double fMeasure = 0.0;
		double precision = 0.0;
		double recall = 0.0;
		double roc = 0.0;
				
		for(IterationResult ir: results) {
			accuracy += ir.getAccuracy();
			error += ir.getError();
			fMeasure += ir.getfMeasure();
			precision += ir.getPrecision();
			recall += ir.getRecall();
			roc += ir.getRoc();
		}
		
		averageResult.setAccuracy(accuracy / num);
		averageResult.setError(error / num);
		averageResult.setfMeasure(fMeasure / num);
		averageResult.setPrecision(precision / num);
		averageResult.setRecall(recall / num);
		averageResult.setRoc(roc / num);
	}
	
	public long getTimeElapsed() {
		return this.end - this.begin;
	}
	
	public int getNumIterations() {
		return numIterations;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

	public String getTrainDataset() {
		return trainDataset;
	}

	public void setTrainDataset(String trainDataset) {
		this.trainDataset = trainDataset;
	}

	public String getTestDataset() {
		return testDataset;
	}

	public void setTestDataset(String testDataset) {
		this.testDataset = testDataset;
	}

	public ArrayList<IterationResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<IterationResult> results) {
		this.results = results;
	}

	public IterationResult getAverageResult() {
		return averageResult;
	}

	public void setAverageResult(IterationResult averageResult) {
		this.averageResult = averageResult;
	}
	
	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public void showResult() {
		System.out.println(buildResultString());
	}

	public String getResult() {
		return buildResultString();
	}
	
	private String buildMetrics() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		sb.append("@TRAIN-DATASET: " + trainDataset + "\n");
		sb.append("@TR-DATASET: " + trainDataset + "\n");
		sb.append("@Iterations  : " + numIterations + "\n");
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n\t\t");
		sb.append("accura" + "\t\t");
		sb.append("error " + "\t\t");
		sb.append("fmeasu" + "\t\t");
		sb.append("precis" + "\t\t");
		sb.append("recall" + "\t\t");
		sb.append("ROC" + "\t\t\n");
		
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		for(int i = 0; i < results.size();i++) {
			sb.append("fold" + (i+1) + ":\t\t");
			sb.append(results.get(i).onlyValuesToString() +"\n");
		}
		sb.append("\n");
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		sb.append("AVERAG" + "\t\t");
		sb.append(averageResult.onlyValuesToString() +"\n");
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		return sb.toString();
	}
	
	private String buildTime() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		sb.append("BEGIN: \t" + DateUtils.fromLongToDateAsString(this.begin));
		sb.append("\n");
		sb.append("END: \t" + DateUtils.fromLongToDateAsString(this.end));
		sb.append("\n");
		sb.append("\n");
		sb.append("TIME ELAPSED:\t" + getTimeElapsed());
		sb.append("\n");
		sb.append("------------------------------------------------------------------------------------------------");
		sb.append("\n");
		return sb.toString(); 
	}
	
	private String buildResultString() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildMetrics());
		sb.append(buildTime());
		return sb.toString();
	}
	
}
