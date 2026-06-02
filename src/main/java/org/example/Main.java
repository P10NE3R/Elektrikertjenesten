package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
         String response = ContractingWorksApi.queryGraphQL("{ __typename }");
        System.out.println(response);
    }
}
