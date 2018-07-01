package com.golars.bean;

import java.util.ArrayList;
import java.util.List;

public class DocumentDetails {
	List<KeyValue> docDetails = new ArrayList<KeyValue>();

	public List<KeyValue> getDocDetails() {
		return docDetails;
	}

	public void setDocDetails(List<KeyValue> docDetails) {
		this.docDetails = docDetails;
	}

}
