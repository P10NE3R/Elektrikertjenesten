package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            String response = ContractingWorksApi.queryGraphQL("{ __typename }");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}