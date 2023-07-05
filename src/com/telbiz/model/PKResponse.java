package com.telbiz.model;

public class PKResponse {
	public String getPartitionKey() {
		return PartitionKey;
	}
	public void setPartitionKey(String partitionKey) {
		PartitionKey = partitionKey;
	}
	public String getRangeKey() {
		return RangeKey;
	}
	public void setRangeKey(String rangeKey) {
		RangeKey = rangeKey;
	}
	public String PartitionKey ;
	public String RangeKey ;
}
