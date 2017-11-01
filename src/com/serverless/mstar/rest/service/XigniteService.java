package com.serverless.mstar.rest.service;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.mstar.domain.BaseAPIDomainObject;
import com.serverless.mstar.domain.BuyOrder;
import com.serverless.mstar.domain.ExchangeResult;
import com.serverless.mstar.domain.MarketMoverResults;
import com.serverless.mstar.domain.XCompanyRecommendations;
import com.serverless.mstar.domain.globalnews.GlobalNewsListSectors;
import com.serverless.mstar.domain.globalnews.GlobalNewsTodaysMarketHeadlines;

public class XigniteService {

	RestTemplate restTemplate = new RestTemplate();

	public ExchangeResult getExchanges() {
		return this.restTemplate.getForObject(
				"https://factsetestimates.xignite.com/xFactSetEstimates.json/ListExchanges?&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				ExchangeResult.class);

	}

	public String getExchangesAsStr() {
		return this.restTemplate.getForObject(
				"https://factsetestimates.xignite.com/xFactSetEstimates.json/ListExchanges?&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);

	}

	// Global News- list Sectors - String
	public String getGlobalNewsListSectorsAsStr() {
		return this.restTemplate.getForObject(
				"https://globalnews.xignite.com/xGlobalNews.json/ListSectors?&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);

	}

	// Global News- Today's Market Headlines - String
	public String getGlobalNewsTodaysMarketHeadlinesAsStr() {
		return this.restTemplate.getForObject(
				"https://globalnews.xignite.com/xGlobalNews.json/GetTodaysMarketHeadlines?&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);

	}

	public String getEstimatesAsStr() {
		return this.restTemplate.getForObject(
				"https://factsetestimates.xignite.com/xFactSetEstimates.json/GetEstimates?IdentifierType=Symbol&Identifiers=MSFT,GOOG&EstimateTypes=EPS,Sales&ReportType=Annual&EstimateFiscalPeriod=2018FY&AsOfDate=10/25/2017&UpdatedSince=&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);
	}

	public String getRecommendationsAsStr() {
		return this.restTemplate.getForObject(
				"https://factsetestimates.xignite.com/xFactSetEstimates.json/GetLatestRecommendationSummaries?IdentifierType=Symbol&Identifiers=MSFT,GOOG&UpdatedSince=&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);
	}

	public String getTopMoversAsStr(String exchangename) {
		return this.restTemplate.getForObject(
				"https://globalquotes.xignite.com/v3/xGlobalQuotes.json/GetTopMarketMovers?MarketMoverType=PercentGainers&NumberOfMarketMovers=3&Exchanges="+exchangename+"&_token=AE4A02E0271A4E77B78B314AEE9A132D",
				String.class);
	}
	
	
	public void saveToDynamo(BuyOrder buyOrder){
		try {
			
			String order=new ObjectMapper().writeValueAsString(buyOrder);
			System.out.println("order JSON is "+order);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<String>(order,headers);
			
			
			
			
			
			String ans=this.restTemplate.postForObject("https://fpdv1komk2.execute-api.us-east-1.amazonaws.com/prod/portfolio", entity,String.class);
			System.out.println("Answer is "+ans);
			
			new SNSService().publish(buyOrder);
			
			//System.out.println("ststus is "+entity.getStatusCode());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String s[]) throws JsonParseException, JsonMappingException, IOException {
		XigniteService svc = new XigniteService();
	  //  System.out.println(svc.getTopMoversAsStr("XNYS"));	
	   // new ObjectMapper().readValue(svc.getTopMoversAsStr("XNYS"), MarketMoverResults.class);
	   svc.saveToDynamo(new BuyOrder("TROW", "Abhay", "100"));
	
	}
		/*String str = svc.getRecommendationsAsStr();
		System.out.println("str is = \n" + str);
		ObjectMapper mapper = new ObjectMapper();
		// List<XCompanyEstimates> resList =
		// mapper.readValue(str,List<XCompanyEstimates>.class>);
		XCompanyRecommendations[] myObjects = null;
		try {
			myObjects = mapper.readValue(str, XCompanyRecommendations[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (myObjects != null) {
			System.out.println(myObjects.toString());
		}
		// List<XCompanyEstimates> myObjects = mapper.readValue(str, new
		// TypeReference<List<XCompanyEstimates>>(){});
	}

	/*
	 * public static void main(String s[]){
	 * System.out.println("exchanges r "+new
	 * XigniteService().getGlobalNewsTodaysMarketHeadlinesAsStr()); }
	 */

}
