package com.nuvemite.cms.planner.geo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyMatrixResponse(List<List<MatrixCell>> sources_to_targets) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MatrixCell(Integer distance, Integer time, Integer source_index, Integer target_index) {}
}
