package com.voltras.ppob.service.components;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;

public interface ExpressionEvaluator {
	default String evaluate(String variableName, String expression, Object variables) {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		ELContext context = new StandardELContext(factory);
		ValueExpression valueExpression = factory.createValueExpression(variables, Object.class);
		context.getVariableMapper().setVariable(variableName, valueExpression);
		
		return evaluate(factory, context, expression);
	}
	
	default String evaluate(ExpressionFactory factory, ELContext context, String expression) {
		if (!expression.startsWith("${") && !expression.endsWith("}")) {
			expression = "${%s}".formatted(expression);
		}
		return factory.createValueExpression(context, expression, String.class).getValue(context).toString();
	}

}
