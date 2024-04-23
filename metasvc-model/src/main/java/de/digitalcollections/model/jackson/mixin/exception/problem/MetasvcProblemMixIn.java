package de.digitalcollections.model.jackson.mixin.exception.problem;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NONE)
public interface MetasvcProblemMixIn {}
