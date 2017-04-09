package ru.makar.simplexsolution.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SimplexSolution {
    private double[][] simplexTable;
    private boolean isMaximization;
    private boolean isMTypeTask;
    private int n;
    private int m;

    public void newInstanse(String equation, ArrayList<String> conditions) throws Exception {
        if (equation.isEmpty()) {
            throw new Exception("Отсутствует уравнение, пожалуйста введите его.");
        }
        equation = equation.replaceAll(" ", "");
        if (equation.endsWith("=minz") || equation.endsWith("=maxz")) {
            equation = equation.split("=")[1] + "=" + equation.split("=")[0];
        }
        for (int i = 0; i < conditions.size(); i++) {
            conditions.set(i, conditions.get(i).replaceAll(" ", ""));
        }
        try {
            parse(equation, conditions);
        } catch (Exception e) {
            throw new Exception("Ошибка при синтаксическом анализе."
                                        + "\nПожалуйста, проверьте правильность введенных вами данных.");
        }
    }

    public String getSolution() throws Exception {
        if (isMTypeTask) {
            throw new Exception("Невозможно решить данным методом."
                                        + "\nОграничения содержат равенства."
                                        + "\nДанная задача решается методом искуственного базиса.");
        }
        int count = 0;
        while (!checkOnSolution()) {
            int newBasis = findNewBasis(); //Разрешающий столбец
            int oldBasis = findOldBasis(newBasis); //Разрешающая строка
            double permissiveElement = simplexTable[oldBasis][newBasis]; //Разрешающий элемент
            simplexTable[oldBasis][0] = newBasis;
            simplexTable[oldBasis][1] = simplexTable[0][newBasis];
            double newSimplexTable[][] = new double[m + 2][n + 3];
            System.arraycopy(simplexTable[0], 0, newSimplexTable[0], 0, n + 3);
            for (int i = 1; i < m + 2; i++) {
                newSimplexTable[i][0] = simplexTable[i][0];
                newSimplexTable[i][1] = simplexTable[i][1];
            }
            for (int i = 1; i < m + 2; i++) {
                for (int j = 2; j < n + 3; j++) {
                    if (i == oldBasis && j == newBasis) {
                        newSimplexTable[i][j] = 1;
                    } else if (i != oldBasis && j == newBasis) {
                        newSimplexTable[i][j] = 0;
                    } else if (i == oldBasis) {
                        newSimplexTable[i][j] = simplexTable[i][j] / permissiveElement;
                    } else {
                        newSimplexTable[i][j] =
                                simplexTable[i][j] - simplexTable[oldBasis][j] * simplexTable[i][newBasis] / permissiveElement;
                    }
                }
            }
            simplexTable = newSimplexTable;
            count++;
        }
        if (count == 0) {
            return "Нет решения";
        }
        double targetFunctionValue = new BigDecimal(simplexTable[m + 1][2]).setScale(3, RoundingMode.HALF_UP)
                                                                           .doubleValue();
        StringBuilder result = new StringBuilder((isMaximization ? "maxZ = " : "minZ = ") + targetFunctionValue);
        result.append("\nПри:\n");
        for (int i = 1; i < m + 1; i++) {
            if (simplexTable[i][0] - 2 <= (n - m)) {
                result.append("x")
                      .append(Math.round(simplexTable[i][0] - 2))
                      .append(" = ")
                      .append(new BigDecimal(simplexTable[i][2]).setScale(
                              3,
                              RoundingMode.HALF_UP))
                      .append("\n");
            }
        }
        return result.toString();
    }

    private void parse(String equation, ArrayList<String> conditions) {
        equation = equation.toLowerCase();
        if (equation.startsWith("maxz=") || equation.startsWith("minz=")) {
            isMaximization = equation.startsWith("maxz=");
            equation = equation.split("=")[1];

        }
        ArrayList<String> equationVars = toPieces(equation);
        n = equationVars.size();
        m = 0;
        String regEx = "[<>]=";
        int equalityCounter = 0;
        for (int i = 0; i < conditions.size(); i++) {
            String condition = conditions.get(i);
            if (condition.contains("<=")) {
                conditions.set(i, condition.split(regEx)[0] + "+x" + (++n) + "=" + condition.split(regEx)[1]);
                m++;
            } else if (condition.contains(">=")) {
                conditions.set(i, condition.split(regEx)[0] + "-x" + (++n) + "=" + condition.split(regEx)[1]);
                m++;
            } else {
                equalityCounter++;
            }

        }
        isMTypeTask = equalityCounter > 0;
        simplexTable = new double[m + 2][n + 3];
        simplexTable[0][0] = Double.NaN;
        simplexTable[0][1] = Double.NaN;
        simplexTable[0][2] = Double.NaN;
        simplexTable[m + 1][0] = Double.NaN;
        simplexTable[m + 1][1] = Double.NaN;
        String coefficient;
        for (String eqVar : equationVars) {
            int ind = Integer.parseInt(eqVar.split("x")[1]);
            coefficient = eqVar.split("x")[0];
            double val = coefficient.isEmpty() ? 1 : ("-".equals(coefficient) ? -1 : Double.parseDouble(coefficient));
            simplexTable[0][ind + 2] = val;
        }
        for (int i = 1; i <= m; i++) {
            simplexTable[i][0] = i + 2 + (n - m);
            simplexTable[i][1] = simplexTable[0][i + 2 + (n - m)];

        }
        for (int i = 1; i <= m; i++) {
            ArrayList<String> condVars = toPieces(conditions.get(i - 1).split("=")[0]);
            for (String condVar : condVars) {
                int index = Integer.parseInt(condVar.split("x")[1]);
                coefficient = condVar.split("x")[0];
                double val = coefficient.isEmpty() ? 1 :
                        ("-".equals(coefficient) ? -1 : Double.parseDouble(coefficient));
                simplexTable[i][index + 2] = val;
            }
        }
        for (int i = 1; i <= m; i++) {
            simplexTable[i][2] = Double.parseDouble(conditions.get(i - 1).split("=")[1]);
        }
        for (int j = 3; j < n + 3; j++) {
            double val = 0;
            for (int i = 1; i <= m; i++) {
                val += simplexTable[i][j] * simplexTable[i][1];
            }
            val -= simplexTable[0][j];
            simplexTable[m + 1][j] = val;
        }
    }

    private ArrayList<String> toPieces(String str) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder var = new StringBuilder();
        for (char sym : str.toCharArray()) {
            if (sym == '-' && (var.length() == 0)) {
                var.append(sym);
            } else if (sym == '+' || sym == '-') {
                result.add(var.toString());
                var = new StringBuilder(sym == '-' ? "-" : "");
            } else {
                var.append(sym);
            }
        }
        result.add(var.toString());
        return result;
    }

    private boolean checkOnSolution() {
        for (int j = 3; j < n + 3; j++) {
            if (isMaximization) {
                if (simplexTable[m + 1][j] < 0) {
                    return false;
                }
            } else {
                if (simplexTable[m + 1][j] > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int findNewBasis() {
        int result = 0;
        double max = Double.MIN_VALUE;
        for (int j = 3; j < n + 3; j++) {
            double val = simplexTable[m + 1][j];
            if (isMaximization) {
                if (val < 0 && Math.abs(val) > max) {
                    max = Math.abs(val);
                    result = j;
                }
            } else {
                if (val > 0 && val > max) {
                    max = val;
                    result = j;
                }
            }
        }
        return result;
    }

    private int findOldBasis(int newBasis) {
        int result = 0;
        double min = Double.MAX_VALUE;
        for (int i = 1; i < m + 1; i++) {
            if (simplexTable[i][newBasis] == 0) {
                continue;
            }
            double val = simplexTable[i][2] / simplexTable[i][newBasis];
            if (val > 0 && val < min) {
                min = val;
                result = i;
            }
        }
        return result;
    }
}