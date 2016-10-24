package com.art2cat.dev.moonlightnote.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by art2cat
 * on 8/27/16.
 */
public class ExpressionUtil {

    public static double evaluateExpression(String input) throws IllegalExpressionException {
        int lengthOfInput = input.length();
        int numOfOperator = 0;
        int q_left = 0;//只能用一对括号
        int q_right = 0;
        //List<String> nums=new ArrayList<String>();
        double nums[];
        double result = 0;
        char operator[];
        String temp = "";
        String output = "";
        char char_input[];
        char_input = new char[input.length()];
        char_input = input.toCharArray();

        try {

            if (input.contains("(") && input.contains(")")) {
                for (int i = 0, temp1 = 0; i < lengthOfInput; i++) {
                    if (char_input[i] == '+' || char_input[i] == '-' || char_input[i] == '×' || char_input[i] == '÷')
                        temp1++;
                    if (char_input[i] == '(') {
                        q_left = temp1;
                    }
                    if (char_input[i] == ')') {
                        q_right = temp1;
                    }
                }
                input = input.replaceAll("\\(|\\)", "");
                System.out.println(input);

                lengthOfInput = input.length();
                char_input = new char[input.length()];
                char_input = input.toCharArray();
                System.out.println(q_left + "\\\\\\\\\\\\" + q_right);
            }


            for (int i = 0; i < lengthOfInput; i++) {//计算符号数
                if (char_input[i] == '+' || char_input[i] == '-' || char_input[i] == '×' || char_input[i] == '÷') {
                    numOfOperator++;
                    if (i < lengthOfInput - 1) {
                        if (char_input[i + 1] == '-' || char_input[i + 1] == '（') {
                            numOfOperator--;//负号符号数组长度减1
                        }
                    }
                }
            }
            operator = new char[numOfOperator];//初始化数组
            nums = new double[numOfOperator + 1];


            for (int i = 0, temp2_int = 0; i < lengthOfInput; i++)//将数字放入数组3+3+3
            {
                if (char_input[0] == '-') {
                    temp = "-";
                }

                if (char_input[i] >= '0' || char_input[i] <= '9') {
                    temp += char_input[i];
                    if (temp2_int == numOfOperator)
                        nums[temp2_int] = Double.valueOf(temp);
                }


                if (char_input[i] == '+' || char_input[i] == '-' || char_input[i] == '×' || char_input[i] == '÷') {
                    if (i > 0)//
                    {
                        nums[temp2_int] = Double.valueOf(temp.substring(0, temp.length() - 1));
                        temp = "";
                        temp2_int++;
                    }
                    if (i < lengthOfInput - 1 && char_input[i + 1] == '-') {
                        temp = "-";
                        i++;
                    }
                    //System.out.println("nums:"+nums[temp2_int]);
                }

            }
            for (int i = 0, temp_int = 0; i < lengthOfInput; i++)//将符号放入数组
            {

                if (char_input[i] == '+' || char_input[i] == '-' || char_input[i] == '×' || char_input[i] == '÷') {
                    operator[temp_int] = char_input[i];
                    temp_int++;

                    if (char_input[i + 1] == '-' && i < lengthOfInput - 1) {
                        i++;
                    }
                }
            }


            if (q_left != 0) {
                double result1 = 0;
                result1 = nums[q_left];
                for (int i = q_left; i < q_right; i++) {
                    if (operator[i] == '×' || operator[i] == '÷') {
                        if (operator[i] == '×')
                            result1 *= nums[i + 1];
                        else
                            result1 /= nums[i + 1];

                        operator[i] = '+';
                        nums[i + 1] = 0;
                        if (i < numOfOperator - 1) {
                            if (operator[i + 1] == '×' || operator[i + 1] == '÷') {
                                nums[i + 1] = nums[i];
                                nums[i] = 0;
                            }
                        }
                        System.out.println("()()()" + nums[i]);
                    }
                    if (operator[i] == '+')
                        result1 += nums[i + 1];
                    else if (operator[i] == '-')
                        result1 -= nums[i + 1];
                    nums[i + 1] = 0;
                    if (i < numOfOperator - 1) {
                        if (operator[i + 1] == '+' || operator[i + 1] == '-') {
                            nums[i + 1] = nums[i];
                            nums[i] = 0;
                        }
                    }

                    System.out.println("()()()" + nums[i]);
                }
                nums[q_left] = result1;
                for (int i = q_left + 1; i < q_right; i++)
                    nums[i] = 0;
            }


            for (int i = 0; i < numOfOperator; i++) {
                if (operator[i] == '×' || operator[i] == '÷') {
                    if (operator[i] == '×')
                        nums[i] *= nums[i + 1];
                    else
                        nums[i] /= nums[i + 1];
                    operator[i] = '+';
                    nums[i + 1] = 0;
                    if (i < numOfOperator - 1) {
                        if (operator[i + 1] == '×' || operator[i + 1] == '÷') {
                            nums[i + 1] = nums[i];
                            nums[i] = 0;
                        }
                    }
                }
            }
            result = nums[0];
            for (int i = 0; i < numOfOperator; i++) {
                //System.out.println(nums[i+1]);
                if (operator[i] == '+')
                    result += nums[i + 1];
                else if (operator[i] == '-')
                    result -= nums[i + 1];
                else if (operator[i] == '×')
                    result *= nums[i + 1];
                else if (operator[i] == '÷')
                    result /= nums[i + 1];
                output += String.valueOf(nums[i]) + operator[i];

                if (i == numOfOperator - 1)
                    output += String.valueOf(nums[i + 1]);
            }
            System.out.println("原式" + input);
            System.out.println("变式" + output);
            System.out.println("result:" + result);
            return result;
        } catch (IllegalExpressionException e) {
            e.printStackTrace();
        }
        return result;
    }

    class IllegalExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public IllegalExpressionException() {

        }

        public IllegalExpressionException(String info) {
            super(info);
        }
    }
}
