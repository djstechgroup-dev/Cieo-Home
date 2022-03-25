package com.kinetise.helpers.math;

import java.text.DecimalFormat;
import java.util.Stack;
import java.util.StringTokenizer;

public class ExpresionEvaluator {
    public static String evaluate(String testExpresion) {
        String result;
        try {
            Stack<Token> onp = buildOnpRepresentation(testExpresion);
            Token top = onp.pop();
            result = formatDouble(top.evaluate(onp));
        } catch(Exception e){
            result = "NaN";
        }

        return result;
    }

    private static Stack<Token> buildOnpRepresentation(String testExpresion) throws NotANumberException{
        Stack<Token> stack = new Stack<Token>();
        StringTokenizer st = new StringTokenizer(testExpresion, Operation.symbolList, true);
        Stack<Token> onp = new Stack<Token>();
        boolean previousTokenWasOperator = false;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            Token token = Token.getToken(s);
            if (s.equals("(")) {
                stack.push(token);
            } else if (s.equals(")")) {
                while (!(stack.peek().getPriority() == -1)) {
                    onp.push(stack.pop());
                }
                stack.pop();
            }else if (token.isOperation()) {
                while (!stack.empty() && stack.peek().getPriority() >= token.getPriority()) {
                    onp.push(stack.pop());
                }
                if(previousTokenWasOperator)
                    throw new NotANumberException();
                previousTokenWasOperator = true;
                stack.push(token);
            }
            else {
                previousTokenWasOperator = false;
                onp.push(token);
            }
        }
        while (!stack.empty()) {
            onp.push(stack.pop());
        }

        return onp;
    }

    private static abstract class Token{

        public static Token getToken(String s){
            if (Operation.isOperation(s))
                return new Operation(s);
            else
                return new Value(s);
        }

        abstract double evaluate(Stack<Token> stack) throws NotANumberException;
        abstract int getPriority();
        abstract boolean isOperation();
    }

    private static class Value extends Token{

        Double val;
        boolean isCorrectValue = true;

        public Value(String s){
            try {
                val = Double.parseDouble(s);
            } catch (NumberFormatException ex) {
                isCorrectValue=false;
            }
        }

        @Override
        double evaluate(Stack<Token> stack) throws NotANumberException{
            if(!isCorrectValue)
                throw new NotANumberException();
            return val;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean isOperation() {
            return false;
        }
    }

    private static class Operation extends Token{
        String symbol;

        Operation(String op){
            symbol=op;
        }

        public static final String symbolList = "+-*:/^()";

        public static boolean isOperation(String s){
            return symbolList.contains(s);
        }

        public boolean isOperation(){
            return true;
        }

        public int getPriority(){
            if(symbol.equals("+")||symbol.equals("-"))
                return 1;
            if(symbol.equals("*")||symbol.equals("/"))
                return 2;
            if(symbol.equals("^"))
                return 3;
            if(symbol.equals("("))
                return -1;
            if(symbol.equals(")"))
                return -2;
            return 0;
        }

        double evaluate(Stack<Token> stack) throws NotANumberException{
            double v1;
            double v2;
            if(symbol.equals("+")) {
                v1 = getNextValue(stack);
                v2 = getNextValue(stack);
                return v2+v1;
            }
            if(symbol.equals("-")){
                v1 = getNextValue(stack);
                v2 = getNextValue(stack);
                return v2-v1;
            }
            if(symbol.equals("*")){
                v1 = getNextValue(stack);
                v2 = getNextValue(stack);
                return v2*v1;
            }
            if(symbol.equals(":")||(symbol.equals("/"))){
                v1 = getNextValue(stack);
                v2 = getNextValue(stack);
                return v2/v1;
            }
            if(symbol.equals("^")){
                v1 = getNextValue(stack);
                v2 = getNextValue(stack);
                return Math.pow(v2,v1);
            }

            return 0d;
        }

        private double getNextValue(Stack<Token> stack) throws NotANumberException {
            if(stack.isEmpty())
                return 0d;
            return stack.pop().evaluate(stack);
        }
    }

    private static String formatDouble(double result) {
        DecimalFormat format = new DecimalFormat("0.##");
        return format.format(result);
    }

    public static class NotANumberException extends Exception{

    }
}

