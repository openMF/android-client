package com.mifos.exceptions;

public class ShortOfLengthException extends Exception{

    private int minimumCharacters;
    private String inputField;
    public ShortOfLengthException(){
        super();
    }

    public ShortOfLengthException(String inputField,int minimumCharacters){
        this.minimumCharacters = minimumCharacters;
        this.inputField = inputField;
    }

    @Override
    public String toString() {
        return inputField +" Field Expects at least " + minimumCharacters
                + " characters";
    }




}
