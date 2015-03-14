package com.hunnymustard.ssbl.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("searchService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class SearchServiceHibernate implements SearchService {

}
