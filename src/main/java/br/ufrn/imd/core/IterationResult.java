package br.ufrn.imd.core;

import br.ufrn.imd.utils.NumberUtils;

public class IterationResult {

	private double accuracy;
	private double error;
	private double fMeasure;
	private double precision;
	private double recall;
	private double roc;
	
	private Integer seed;
	private String trainTestProportion;
	
	public IterationResult() {
		
	}
	
	/**
	 * @param seed
	 * @param traingTestProportion train and test with format "70-30" (example)
	 */
	public IterationResult(Integer seed, String trainTestPorportion) {
		this.seed = seed;
		this.trainTestProportion = new String(trainTestProportion);
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getfMeasure() {
		return fMeasure;
	}

	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}
	
	public double getRoc() {
		return roc;
	}

	public void setRoc(double roc) {
		this.roc = roc;
	}

	public Integer getSeed() {
		return seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public String getTrainTestProportion() {
		return trainTestProportion;
	}

	/**
	 * 
	 * @param traingTestProportion train and test with format "70-30" (example)
	 */
	public void setTrainTestProportion(String trainTestProportion) {
		this.trainTestProportion = new String(trainTestProportion);
	}

	public String onlyValuesToString() {

		StringBuilder sb = new StringBuilder();
		sb.append(NumberUtils.formatValue(accuracy) + "\t\t");
		sb.append(NumberUtils.formatValue(error) + "\t\t");
		sb.append(NumberUtils.formatValue(fMeasure) + "\t\t");
		sb.append(NumberUtils.formatValue(precision) + "\t\t");
		sb.append(NumberUtils.formatValue(recall) + "\t\t");
		sb.append(NumberUtils.formatValue(roc) + "\t\t");

		return sb.toString();
	}

}
