package com.nprinting.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.nprinting.model.nprinting.request.Selection;
import com.nprinting.utils.StringUtils;

public class FilterService {

    private static final String GROUP_SEPARATOR = "#";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String SELECTED_VALUES_SEPARATOR = "&";
    private static final String WITH_GROUP_SEPARATOR = ",";
    private final Function<String, List<String>> toListOfFilterElements = filterGroup -> StringUtils.splitSafely(filterGroup,
        WITH_GROUP_SEPARATOR);
    private Predicate<String> notEmptyFilter = filterGroup -> !"".equals(filterGroup);
    private final String filterArguments;

    public FilterService(String filterArguments) {
        this.filterArguments = filterArguments;
    }

    public List<Selection> buildSelections() {
        final List<String> filterGroups = StringUtils.splitSafely(filterArguments, GROUP_SEPARATOR);
        final List<List<String>> rawSelections = buildRawSelections(filterGroups);
        return rawSelections.stream().map(this::buildSelection).collect(toList());
    }

    private List<List<String>> buildRawSelections(final List<String> filterGroups) {
        return filterGroups.stream().filter(notEmptyFilter).map(toListOfFilterElements).collect(toList());
    }

    private Selection buildSelection(final List<String> filterGroup) {
        final Selection parsedSelection = new Selection();
        for (String filterFieldNameAndValue : filterGroup) {
            final List<String> nameAndValue = StringUtils.splitSafely(filterFieldNameAndValue, KEY_VALUE_SEPARATOR);
            final String selectionFieldNameToSet = StringUtils.trim(nameAndValue.get(0));
            final String selectionFieldValueToSet = nameAndValue.get(1);
            switch (selectionFieldNameToSet) {
                case "fieldName":
                    parsedSelection.setFieldName(selectionFieldValueToSet);
                    break;
                case "selectedCount":
                    parsedSelection.setSelectedCount(selectionFieldValueToSet);
                    break;
                case "selectedValues":
                    parsedSelection.setSelectedValues(buildSelectedValues(selectionFieldValueToSet));
                    break;
                case "isNumeric":
                    parsedSelection.setIsNumeric(selectionFieldValueToSet);
                    break;
                default:
                    throw new RuntimeException("Unknown option name " + selectionFieldNameToSet);
            }
        }
        return parsedSelection;
    }

    private List<String> buildSelectedValues(final String selectionFieldValueToSet) {
        return StringUtils.splitSafely(selectionFieldValueToSet, SELECTED_VALUES_SEPARATOR);
    }
}
