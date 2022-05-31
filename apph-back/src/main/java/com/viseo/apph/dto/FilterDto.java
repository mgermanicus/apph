package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import java.io.InvalidObjectException;

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

    public String getOperator() {
        return operator;
    }

    public FilterDto setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FilterDto setValue(String value) {
        this.value = value;
        return this;
    }

    public String getFieldToSql() throws InvalidObjectException {
        switch (field) {
            case "title":
                return "p.title";
            case "description":
                return "p.description";
            case "creationDate":
                return "p.creationDate";
            case "shootingDate":
                return "p.shootingDate";
            case "tags":
                return "'" + value + "'";
            default:
                throw new InvalidObjectException("champ non reconnu : " + field);
        }
    }

    public String getOperatorToSql() throws InvalidObjectException {
        if (field.equals("title") || field.equals("description")) {
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

    public String getValueToSql() {
        if (field.equals("tags")) {
            return "(select t.name from p.tags t)";
        } else if (operator.equals("contain")) {
            return "'%" + value + "%'";
        }
        return "'" + value + "'";

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
