package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

public class FilterDto implements Comparable<FilterDto> {

    @JsonProperty("field")
    String field;
    @JsonProperty("operator")
    String operator;
    @JsonProperty("value")
    String value;

    public String getField() {
        return field;
    }

    public FilterDto setField(String field) {
        this.field = field;
        return this;
    }

    public FilterDto setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public FilterDto setValue(String value) {
        this.value = value;
        return this;
    }

    public String getFieldToSql(Queue<Object> argQueue) throws InvalidObjectException {
        switch (field) {
            case "title":
                return "p.title";
            case "description":
                return "p.description";
            case "creationDate":
                argQueue.add(LocalDate.parse(value, DateTimeFormatter.ofPattern("MM/d/yyyy")));
                return "p.creationDate";
            case "shootingDate":
                argQueue.add(LocalDate.parse(value, DateTimeFormatter.ofPattern("MM/d/yyyy")));
                return "p.shootingDate";
            case "tags":
                argQueue.add(value);
                return "?" + argQueue.size();
            case "address":
                return "p.address";
            default:
                throw new InvalidObjectException("champ non reconnu : " + field);
        }
    }

    public String getOperatorToSql() throws InvalidObjectException {
        if (field.equals("title") || field.equals("description") || field.equals("address")) {
            if (operator.equals("is") || operator.equals("contain")) {
                return "LIKE";
            }
            throw new InvalidObjectException("Operateur invalide pour un " + field);
        } else if (field.equals("tags")) {
            return "IN";
        } else if (field.equals("creationDate") || field.equals("shootingDate")) {
            switch (operator) {
                case "strictlyInferior":
                    return "<";
                case "strictlySuperior":
                    return ">";
                case "inferiorEqual":
                    return "<=";
                case "superiorEqual":
                    return ">=";
                case "equal":
                    return "=";
                default:
                    throw new InvalidObjectException("Operateur invalide pour un " + field);
            }
        }
        throw new InvalidObjectException("champ non reconnu");
    }

    public String getValueToSql(Queue<Object> argQueue) {
        if (field.equals("tags")) {
            return "(select t.name from p.tags t)";
        } else if (operator.equals("contain")) {
            argQueue.add(value);
            return "'%' || ?" + argQueue.size() + " || '%'";
        }
        if (field.equals("title") || field.equals("description"))
            argQueue.add(value);
        return "?" + argQueue.size();

    }

    @Override
    public int compareTo(@NotNull FilterDto o) {
        if (o == null) {
            return 0;
        }
        int thisField = 0;
        int otherField = 0;
        switch (field) {
            case "title":
                thisField = 1;
                break;
            case "description":
                thisField = 2;
                break;
            case "creationDate":
                thisField = 3;
                break;
            case "shootingDate":
                thisField = 4;
                break;
            case "tags":
                thisField = 5;
        }
        switch (o.field) {
            case "title":
                otherField = 1;
                break;
            case "description":
                otherField = 2;
                break;
            case "creationDate":
                otherField = 3;
                break;
            case "shootingDate":
                otherField = 4;
                break;
            case "tags":
                otherField = 5;
        }
        return thisField - otherField;
    }
}
