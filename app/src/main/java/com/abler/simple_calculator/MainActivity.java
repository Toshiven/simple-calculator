package com.abler.simple_calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find text views and assign them to variables
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        // find buttons and set on click listeners
        findViewById(R.id.c).setOnClickListener(this);
        findViewById(R.id.open_bracket).setOnClickListener(this);
        findViewById(R.id.close_bracket).setOnClickListener(this);
        findViewById(R.id.divide).setOnClickListener(this);
        findViewById(R.id.multiply).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.subtract).setOnClickListener(this);
        findViewById(R.id.equal).setOnClickListener(this);
        findViewById(R.id.zero).setOnClickListener(this);
        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.ac).setOnClickListener(this);
        findViewById(R.id.dot).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {

            // clears everything
            case "AC":
                currentExpression = "";
                solutionTv.setText("");
                resultTv.setText("0");
                break;

                /* evaluate the expression and display results
                   and error handling
                */
            case "=":
                try {
                    double result = evaluateExpression(currentExpression);
                    resultTv.setText(String.valueOf(result));
                } catch (SyntaxException e) {
                    resultTv.setText("Syntax Error");
                } catch (ArithmeticException e) {
                    resultTv.setText("Math Error");
                } catch (Exception e) {
                    resultTv.setText("Error");
                } finally {
                    solutionTv.setText(currentExpression);
                }
                break;

                // clear the last character of the expression
            case "C":
                if (!currentExpression.isEmpty()) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    solutionTv.setText(currentExpression);
                }
                break;

                // appends the button next to the expression
            default:
                currentExpression += buttonText;
                solutionTv.setText(currentExpression);
                break;
        }
    }

    // evaluate the given expression
    double evaluateExpression(String expression) throws SyntaxException, ArithmeticException {
        try {
            return eval(expression);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ArithmeticException) {
                throw new ArithmeticException(e.getMessage());
            } else {
                throw new SyntaxException(e.getMessage());
            }
        }
    }

    // expression for the syntax errors
    public static class SyntaxException extends Exception {
        public SyntaxException(String message) {
            super(message);
        }
    }

    // function for the math expressions
    double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            // gets the next character in the expression
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            // checks if the next character matches the expected character
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            // parse the entire expression
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
            // parse the expressions for addition or subtraction
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // Addition
                    else if (eat('-')) x -= parseTerm(); // Subtraction
                    else return x;
                }
            }

            // parse the term for multiplication or division
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('x')) x *= parseFactor(); // Multiplication
                    else if (eat('%')) {
                        double divisor = parseFactor();
                        if (divisor == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        x /= divisor; // Division
                    } else return x;
                }
            }

            // parse a factor, which can be a number, a parenthesized expression, or unary plus/minus
            double parseFactor() {
                if (eat('+')) return parseFactor(); // Unary plus
                if (eat('-')) return -parseFactor(); // Unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // Parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // Numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }
}
