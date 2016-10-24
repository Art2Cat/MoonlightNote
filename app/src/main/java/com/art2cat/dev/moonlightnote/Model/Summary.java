package com.art2cat.dev.moonlightnote.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by art2cat
 * on 8/26/16.
 */
public class Summary {
    public String income;
    public String output;
    public String balance;

    public Summary() { }

    public Summary(String income, String output, String balance) {
        this.income = income;
        this.output = output;
        this.balance = balance;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("income", income);
        result.put("output", output);
        result.put("balance", balance);

        return result;
    }
}
