package com.empanada.app.webservice.ui.utils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperBuilderImpl implements MapperBuilder{

  private ModelMapper mapper;

  @Autowired
  public MapperBuilderImpl( ModelMapper mapperImpl) {
    mapper = mapperImpl;
  }

  @Override
  public ModelMapper getMapper() {
    if (mapper == null) {
      buildDefaultMapper();
    }
    return mapper;
  }

  public ModelMapper buildDefaultMapper() {
    mapper = new ModelMapper();
    return mapper;
  }
}
