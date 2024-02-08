package com.voltras.ppob.service.components;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;

import org.springframework.stereotype.Component;

@Component
public class PriceEvaluator implements ExpressionEvaluator {
	public Double evaluateCommission(String expression, Double total, Double nta) {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		ELContext context = new StandardELContext(factory);
		
		
		ValueExpression totalExpr = factory.createValueExpression(total, Double.class);
		ValueExpression ntaExpr = factory.createValueExpression(nta, Double.class);
		
		context.getVariableMapper().setVariable("TOTAL", totalExpr);
		context.getVariableMapper().setVariable("NTA", ntaExpr);
		
		
		var evaluatedCommission = evaluate(factory, context, expression);

		return Double.valueOf(evaluatedCommission);
	}

	public Double evaluateAdminFee(String expression, Double total) {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		ELContext context = new StandardELContext(factory);

		ValueExpression totalExpr = factory.createValueExpression(total, Double.class);
		context.getVariableMapper().setVariable("TOTAL", totalExpr);

		var evaluated = evaluate(factory, context, expression);

		return new BigDecimal(evaluated).setScale(0, RoundingMode.CEILING).doubleValue();
	}
}
