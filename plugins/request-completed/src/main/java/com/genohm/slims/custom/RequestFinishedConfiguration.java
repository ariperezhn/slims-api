/*
 * Copyright 2021 Agilent Technologies Inc.
 */

package com.genohm.slims.custom;

public class RequestFinishedConfiguration {

	private String parameterOne;
	private String parameterTwo;
    private String belongsToSample;

	public RequestFinishedConfiguration() {

	}

	public String getParameterOne() {
		return parameterOne;
	}

	public String getParameterTwo() {
		return parameterTwo;
	}
    public String getBelongsToSample() { return belongsToSample ==null ? "cntn_cf_fk_belongsToSample" : belongsToSample; }

	public static RequestFinishedConfiguration getDefault() {
		RequestFinishedConfiguration requestFinishedConfiguration = new RequestFinishedConfiguration();
		requestFinishedConfiguration.parameterOne = "Value 1";
		requestFinishedConfiguration.parameterTwo = "Value 2";
        requestFinishedConfiguration.belongsToSample = "cntn_cf_fk_belongsToSample";
		return requestFinishedConfiguration;
	}


}
