package com.serverless.mstar.lambda.intent.processors;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.events.LexEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.serverless.mstar.domain.BuyOrder;
import com.serverless.mstar.lambda.response.DialogAction;
import com.serverless.mstar.lambda.response.LexResponse;
import com.serverless.mstar.lambda.response.Message;
import com.serverless.mstar.rest.service.XigniteService;

public class BuyIntent extends IntentProcessor{

	@Override
	protected Object validate(LexEvent lexEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object fullFill(LexEvent lexEvent) throws JsonParseException, JsonMappingException, IOException {
		
		String stock=lexEvent.getSessionAttributes().getOrDefault("CompanyAttr","MSFT");
		String name=lexEvent.getCurrentIntent().getSlots().get("name");
		String quantity=lexEvent.getCurrentIntent().getSlots().get("quantity");
		
		new XigniteService().saveToDynamo(new BuyOrder(stock, name, quantity));
		
		DialogAction dialogAction = new DialogAction("Close", "Fulfilled", new Message("PlainText","Order is palced"));
		 
		 return  new LexResponse(dialogAction, lexEvent.getSessionAttributes());
		 
	}

}
