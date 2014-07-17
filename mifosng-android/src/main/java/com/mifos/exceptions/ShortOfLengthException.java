/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.exceptions;

public class ShortOfLengthException extends Exception{

    private int minimumCharacters;
    private String inputField;

    public ShortOfLengthException(String inputField,int minimumCharacters){
        super();
        this.minimumCharacters = minimumCharacters;
        this.inputField = inputField;
    }

    @Override
    public String toString() {
        return inputField +" Field Expects at least " + minimumCharacters
                + " characters";
    }




}
